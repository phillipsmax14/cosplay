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

package org.cosplay.examples.image

import org.cosplay.*
import CPArrayImage.prepSeq
import CPColor.*
import CPPixel.*
import CPKeyboardKey.*
import prefabs.images.*
import prefabs.scenes.*
import prefabs.shaders.*

/*
   _________            ______________
   __  ____/_______________  __ \__  /_____ _____  __
   _  /    _  __ \_  ___/_  /_/ /_  /_  __ `/_  / / /
   / /___  / /_/ /(__  )_  ____/_  / / /_/ /_  /_/ /
   \____/  \____//____/ /_/     /_/  \__,_/ _\__, /
                                            /____/

          2D ASCII JVM GAME ENGINE FOR SCALA3
              (C) 2021 Rowan Games, Inc.
                ALl rights reserved.
*/

/**
  * Code example for image functionality.
  *
  * @see [[CPImage]]
  * @see [[CPArrayImage]]
  */
object CPImageCarouselExample:
    // Images for the carousel.
    private val imgs = Seq(
        CPAardvarkImage.trimBg(),
        CPAlienImage.trimBg(),
        CPAmigaImage,
        CPAtari2080STImage,
        CPBearImage.trimBg(),
        CPBeetleImage,
        CPCastleImage.trimBg(),
        CPGlobeImage,
        CPHelicopterImage.trimBg(),
        CPOceanLinerImage,
        CPPlaneImage.trimBg(),
        CPSaturnImage.trimBg(),
        CPSkullImage.trimBg(),
        CPTruckImage.trimBg(),
        CPGitarImage,
        CPSpeckImage
    )
    private val bgPx = CPPixel('.', C_GRAY2, C_GRAY1)

    class CarouselSprite(img: CPImage, viewDim: CPDim) extends CPSceneObject:
        private final val centerY = (viewDim.height - img.getHeight) / 2
        private final val centerX = (viewDim.width - img.getWidth) / 2
        private final val leftOffScrX = -(img.getWidth + 1)
        private final val rightOffScrX = viewDim.width
        private final val stepX = 1.0f
        private val fadeInShdr = new CPFadeInShader(false, 2000, bgPx, autoStart = false)
        private val fadeOutShdr = new CPFadeOutShader(false, 1000, bgPx, onFinish = _ => setVisible(false))
        private final val shdrs = Seq(fadeInShdr, fadeOutShdr)

        private var x: Float = leftOffScrX.toFloat
        private var step = 0f
        private var targetX = 0

        def isMoving: Boolean = step != 0
        private def move(newX: Int, newStep: Float, newTargetX: Int): Unit =
            x = newX.toFloat
            step = newStep
            targetX = newTargetX
        def placeInCenter(): Unit =
            move(centerX, 0f, centerX)
            setVisible(true)
            fadeInShdr.start()
        def fadeInFromLeft(): Unit =
            move(leftOffScrX, stepX, centerX)
            setVisible(true)
            fadeInShdr.start()
        def fadeInFromRight(): Unit =
            move(rightOffScrX, -stepX, centerX)
            setVisible(true)
            fadeInShdr.start()
        def fadeOutToLeft(): Unit =
            move(centerX, -stepX, leftOffScrX)
            fadeOutShdr.start()
        def fadeOutToRight(): Unit =
            move(centerX, stepX, rightOffScrX)
            fadeOutShdr.start()

        override def getShaders: Seq[CPShader] = shdrs
        override def getX: Int = x.round
        override val getY: Int = centerY
        override val getZ: Int = 0
        override def getDim: CPDim = img.getDim
        override def update(ctx: CPSceneObjectContext): Unit = if getX != targetX then x += step else step = 0
        override def render(ctx: CPSceneObjectContext): Unit = ctx.getCanvas.drawImage(img, getX, getY, getZ)

    /**
      * Entry point for JVM runtime.
      *
      * @param args Ignored.
      */
    def main(args: Array[String]): Unit =
        val maxImgW = imgs.maxBy(_.getDim.width).getWidth
        val maxImgH = imgs.maxBy(_.getDim.height).getHeight

        val ctrlImg = CPArrayImage(
            prepSeq(
                """
                  |              LEFT            RIGHT
                  |          .----..----.    .----..----.
                  |          | <= || A  |    | D  || => |
                  |          `----'`----'    `----'`----'
                  |
                  |[Q] Quit   [Ctrl+Q] FPS Overlay   [Ctrl+L] Open Log
                """),
            (ch, _, _) => ch match
                case c if c.isLetter => c&C_STEEL_BLUE1
                case '|' | '.' | '`' | '-' | '\'' => ch&C_LIME
                case _ => ch.toUpper&C_DARK_ORANGE
        ).trimBg()

        val dim = CPDim(maxImgW + 8 * 2, maxImgH + ctrlImg.getHeight + 4)
        val viewDim = CPDim(dim.width, maxImgH)
        var sprIdx = 0
        val sprs = imgs.map(img => new CarouselSprite(img, viewDim)).toIndexedSeq

        sprs.head.placeInCenter()

        // Off-screen sprite for keyboard control.
        val kbCtrl = new CPKeyboardSprite((ctx, key) => key match
            case KEY_LEFT | KEY_LO_A => // Scroll carousel left.
                val curSpr = sprs(sprIdx)
                if !curSpr.isMoving then
                    curSpr.fadeOutToLeft()
                    sprIdx = if sprIdx == 0 then sprs.size - 1 else sprIdx - 1
                    sprs(sprIdx).fadeInFromRight()
            case KEY_RIGHT | KEY_LO_D => // Scroll carousel right.
                val curSpr = sprs(sprIdx)
                if !curSpr.isMoving then
                    curSpr.fadeOutToRight()
                    sprIdx = if sprIdx == sprs.size - 1 then 0 else sprIdx + 1
                    sprs(sprIdx).fadeInFromLeft()
            // Exit the game on 'q' press.
            case KEY_LO_Q => ctx.exitGame() // ¯\_(ツ)_/¯
            case _ => ()
        )

        val sc = new CPScene("scene", Some(dim), bgPx,
            (
                // Control sprites.
                Seq(
                    // Control image/label.
                    CPStaticImageSprite((dim.width - ctrlImg.getWidth) / 2, dim.height - ctrlImg.getHeight - 2, 0, ctrlImg),
                    kbCtrl,
                    // Just for the initial scene fade-in effect.
                    new CPOffScreenSprite(new CPFadeInShader(true, 1500, bgPx)),
                )
                ++
                // Add carousel images sprites.
                sprs
            ):_*
        )

        // Initialize the engine.
        CPEngine.init(
            CPGameInfo(
                name = "Image Carousel Example",
                devName = "(C) 2021 Rowan Games, Inc.",
                initDim = Some(dim)
            ),
            System.console() == null || args.contains("emuterm")
        )

        // Start the game & wait for exit.
        try CPEngine.startGame(new CPLogoScene("logo", Some(dim), bgPx, List(C_STEEL_BLUE1, C_LIME, C_ORANGE1), "scene"), sc)
        finally CPEngine.dispose()

        sys.exit(0)

