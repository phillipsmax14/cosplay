/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cosplay.games.snake

import org.cosplay.*
import org.cosplay.games.*
import org.cosplay.prefabs.shaders.*
import org.cosplay.prefabs.shaders.CPSlideDirection.*
import org.cosplay.CPFIGLetFont.*
import org.cosplay.CPArrayImage.*
import org.cosplay.CPPixel.*
import org.cosplay.CPColor.*
import org.cosplay.CPKeyboardKey.*
import org.cosplay.prefabs.particles.CPConfettiEmitter
import org.cosplay.prefabs.particles.*
import org.cosplay.prefabs.sprites.{CPBubbleSprite, CPCenteredImageSprite}

/*
   _________            ______________
   __  ____/_______________  __ \__  /_____ _____  __
   _  /    _  __ \_  ___/_  /_/ /_  /_  __ `/_  / / /
   / /___  / /_/ /(__  )_  ____/_  / / /_/ /_  /_/ /
   \____/  \____//____/ /_/     /_/  \__,_/ _\__, /
                                            /____/

          2D ASCII JVM GAME ENGINE FOR SCALA3
              (C) 2021 Rowan Games, Inc.
                All rights reserved.
*/

/**
  * Snake main gameplay scene.
  *
  * @param dim Fixed dimension for this scene.
  */
class CPSnakePlayScene(dim: CPDim) extends CPScene("play", dim.?, BG_PX):
    private val WIN_SCORE = 100
    private var score = 0
    private var go = true
    private var dead = false
    private val borderPx = ' '&&(C1, C1)
    private val scorePx = ' '&&(C2, C2)
    private val bodyPx = ' '&&(C3, C3)
    private val headPx = ' '&&(C4, C4)
    private val yamImgs = new CPArrayImage(
        prepSeq(
            """
              |oO
              |------
              |OO
              |------
              |Oo
              |------
              |oo
              |------
            """).filter(!_.endsWith("------")
        ),
        (ch, _, _) => ch&C1
    ).split(2, 1)
    private val yamAniSeq = CPAnimation.filmStrip("yamAni", 150.ms, imgs = yamImgs).seq
    private def prepDialog(art: String): CPArrayImage =
        new CPArrayImage(
            prepSeq(art),
            (ch, _, _) => ch match
                case '*' => ' ' && (C2, C2)
                case c if c.isLetter || c == '/' => c && (C4, BG_PX.bg.get)
                case _ => ch && (C3, BG_PX.bg.get)
        )
    private val youLostImg = prepDialog:
        """
          |**********************************
          |**                              **
          |**    YOU LOST :-(              **
          |**    ------------              **
          |**                              **
          |**    [SPACE]   Continue        **
          |**    [Q]       Quit            **
          |**    [CTRL+A]  Audio On/OFF    **
          |**    [CTRL+Q]  FPD Overlay     **
          |**    [CTRL+L]  Log Console     **
          |**                              **
          |**********************************
        """
    private val youWonImg = prepDialog:
        """
          |**********************************
          |**                              **
          |**    YOU WON :-)               **
          |**    -----------               **
          |**                              **
          |**    [SPACE]   Continue        **
          |**    [Q]       Quit            **
          |**    [CTRL+A]  Audio On/OFF    **
          |**    [CTRL+Q]  FPD Overlay     **
          |**    [CTRL+L]  Log Console     **
          |**                              **
          |**********************************
        """
    private val yamEmitter = new CPConfettiEmitter(
        () => yamSpr.getX,
        () => yamSpr.getY,
        genSize = 10,
        maxAge = 15,
        CS,
        BG_PX.fg,
        _ => "oO0Xx".rand,
        z = 0
    )
    private val yamPartSpr = CPParticleSprite(emitters = yamEmitter.seq)
    private val scoreSpr = new CPImageSprite(x = 0, y = 0, z = 1, img = mkScoreImage):
        override def update(ctx: CPSceneObjectContext): Unit =
            val canv = ctx.getCanvas
            setX((canv.w - getImage.w) / 2)
    private val scoreH = scoreSpr.getHeight
    private val borderSpr = new CPCanvasSprite:
        override def render(ctx: CPSceneObjectContext): Unit =
            val canv = ctx.getCanvas
            // Draw border.
            canv.drawRect(0, scoreH, CPDim(canv.w, canv.h - scoreH), 1, (_, _) => borderPx)
            canv.drawLine(1, scoreH + 1, 1, canv.h, 1, borderPx)
            canv.drawLine(canv.w - 2, scoreH + 1, canv.w - 2, canv.h, 1, borderPx)
            // Draw score rectangle fill.
            canv.fillRect(0, 0, canv.w, scoreH - 1, 1, (_, _) => scorePx)
    private val yamShdr = CPShimmerShader(false, CS, keyFrame = 7, autoStart = true)
    private val yamSpr = new CPAnimationSprite(anis = yamAniSeq, x = 0, y = 0, z = 0, initAniId = "yamAni", shaders = yamShdr.seq)
    private val snakeSpr: CPCanvasSprite = new CPCanvasSprite:
        private val INIT_SPEED = .5f
        private val yelps = Seq("Yam", "Tasty", "Num", "Okay", "Nice", "Right", "Bam", "Wow", "Yep", "Yes")
        private var snake = nil[(Int, Int)]
        private var dx = 0f
        private var dy = 0f
        private var x = 0f
        private var y = 0f
        private var ateYam = false
        private var speed = INIT_SPEED

        override def onActivate(): Unit =
            // Reset snake sprite on each scene activation.
            snake = Nil
            dx = 0f
            dy = 0f
            speed = INIT_SPEED

        /**
          * Turns snake.
          */
        private def turn(dx: Float, dy: Float): Unit =
            this.dx = dx
            this.dy = dy

        private def isDead(c: CPCanvas): Boolean =
            val (hx, hy) = snake.head
            val rx = (x * 2).round
            val ry = y.round
            // Check for self-bite death.
            snake.tail.exists((a, b) => a == hx && b == hy) ||
            // Check borders.
            ry > c.yMax - 1 || ry < scoreH + 1 || rx < 1 || rx > c.xMax - 3

        /**
          * Drops yam at the random location.
          */
        def dropYam(c: CPCanvas): Unit =
            yamSpr.setX(CPRand.randInt(2, c.xMax - 4))
            yamSpr.setY(CPRand.randInt(scoreH + 1, c.yMax - 1))

        override def update(ctx: CPSceneObjectContext): Unit =
            super.update(ctx)

            if go then
                val canv = ctx.getCanvas
                if snake.isEmpty then
                    // Initialize the snake.
                    val cx = canv.xCenter / 2
                    val cy = canv.yCenter
                    for i <- 0 to 5 do snake +:= cx + i -> cy
                    val (hx, hy) = snake.head
                    x = hx.toFloat
                    y = hy.toFloat
                    if CPRand.coinFlip() then
                        dx = speed
                        dy = 0
                    else
                        dx = 0
                        dy = speed
                    dropYam(canv)

                def youLost(): Unit =
                    youLostSpr.show()
                    if audioOn then youLostSnd.replay(1000.ms)
                    yamSpr.hide()
                    bgSnd.stop(1000.ms)

                // Check for snake death.
                if isDead(canv) then
                    go = false
                    dead = true
                    youLost()
                else
                    // Move snake.
                    x += dx
                    y += dy
                    val xInt = x.round
                    val yInt = y.round
                    val (hx, hy) = snake.head
                    // Check for yam.
                    if yamSpr.getY == hy && (yamSpr.getX - hx * 2).abs <= 1 then
                        score += 1
                        speed += 0.002f
                        // Update score.
                        scoreSpr.setImage(mkScoreImage)
                        // Play yam sound.
                        if audioOn then yamSnd.replay()
                        // Particle effect (for new location).
                        yamPartSpr.resume(reset = true)
                        // Bubble sprite (for current location).
                        val img = FIG_CHUNKY.render(s"${yelps.rand}!", CS.rand, None)
                        val bubbleSpr = new CPBubbleSprite(
                            img = img,
                            // Make sure the bubble fits on the screen.
                            yamSpr.getX.min(canv.xMax - img.w - 2),
                            yamSpr.getY.min(canv.yMax - img.h - 1),
                            z = 0,
                            _ => 0f,
                            _ => -0.3f,
                            BG_PX,
                            durMs = 1000.ms
                        )
                        ctx.addObject(bubbleSpr)
                        if score == WIN_SCORE then
                            go = false
                            dead = false
                            youLost()
                        else
                            ateYam = true
                            dropYam(canv)
                    if hx != xInt || hy != yInt then
                        if !ateYam then snake = snake.dropRight(1) else ateYam = false
                        snake +:= xInt -> yInt
                    ctx.getKbEvent match
                        case Some(evt) =>
                            // Turn snake.
                            evt.key match
                                case KEY_LO_W | KEY_UP => if dy == 0 then turn(0, -speed)
                                case KEY_LO_S | KEY_DOWN => if dy == 0 then turn(0, speed)
                                case KEY_LO_A | KEY_LEFT => if dx == 0 then turn(-speed, 0)
                                case KEY_LO_D | KEY_RIGHT => if dx == 0 then turn(speed, 0)
                                case _ => ()
                        case None => ()
            else
                if ctx.isKbKey(KEY_SPACE) then
                    youLostSnd.stop(500.ms)
                    fadeOutShdr.start(ctx => ctx.switchScene("title", true))

        override def render(ctx: CPSceneObjectContext): Unit =
            !>(snake.nonEmpty)
            val canv = ctx.getCanvas
            def draw(xy: (Int, Int), px: CPPixel): Unit =
                canv.drawPixel(px, xy._1 * 2, xy._2, 2)
                canv.drawPixel(px, xy._1 * 2 + 1, xy._2, 2)
            val hpx = if !go && dead then headPx.withChar('8').withFg(C_BLACK) else headPx
            val bpx = if !go && dead then bodyPx.withChar('X').withFg(C_BLACK) else bodyPx
            // Draw snake.
            draw(snake.head, hpx) // Head.
            snake.tail.foreach(draw(_, bpx)) // Rest of the body.

    // Announcements.
    private val lostWonShdr = CPSlideInShader.sigmoid(LEFT_TO_RIGHT, false, 1000.ms, BG_PX)
    private val youLostSpr = new CPCenteredImageSprite(img = youLostImg, z = 6, shaders = lostWonShdr.seq)
    private val youWonSpr = new CPCenteredImageSprite(img = youWonImg, z = 6, shaders = lostWonShdr.seq)

    private val bgSnd = CPSound(s"sounds/games/snake/snake.wav", vol = 0.7f)
    private val yamSnd = CPSound(s"sounds/games/snake/yam.wav")
    private val youLostSnd = CPSound(s"sounds/games/snake/you_lost.wav")
    private val youWonSnd = CPSound(s"sounds/games/snake/you_won.wav")

    /** Creates score image. */
    private def mkScoreImage: CPImage = FIG_ANSI_REGULAR.render(s"SCORE : $score", C3).trimBg()

    // Shaders.
    private val fadeInShdr = CPFadeInShader(entireFrame = true, 500.ms, BG_PX)
    private val fadeOutShdr = CPFadeOutShader(entireFrame = true, 500.ms, BG_PX)

    addObjects(
        new CPOffScreenSprite(Seq(fadeInShdr, fadeOutShdr)),
        // Handle 'Q' press globally for this scene.
        CPKeyboardSprite(KEY_LO_Q, _.exitGame()),
        // Toggle audio on 'CTRL+A' press.
        CPKeyboardSprite(KEY_CTRL_A, _ => toggleAudio()),
        scoreSpr,
        borderSpr,
        snakeSpr,
        yamSpr,
        yamPartSpr,
        youWonSpr,
        youLostSpr
    )

    private def stopAudio(): Unit =
        bgSnd.stop()
        yamSnd.stop()
        youLostSnd.stop()
        youWonSnd.stop()

    private def toggleAudio(): Unit =
        if audioOn then
            stopAudio() // Stop all sounds.
            audioOn = false
        else
            bgSnd.loop(2000.ms)
            audioOn = true

    override def onDeactivate(): Unit = stopAudio()
    override def onActivate(): Unit =
        score = 0
        go = true
        dead = false
        if audioOn then bgSnd.loop(2000.ms) // Start background audio.
        youWonSpr.hide()
        youLostSpr.hide()
