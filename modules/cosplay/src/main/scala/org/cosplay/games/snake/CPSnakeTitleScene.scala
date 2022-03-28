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

import org.cosplay.games.*
import org.cosplay.*
import CPColor.*
import CPArrayImage.*
import prefabs.shaders.*
import CPPixel.*
import CPKeyboardKey.*

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
  * Title scene for snake game.
  */
object CPSnakeTitleScene extends CPScene("title", None, BG_PX):
    private val introSnd = CPSound(s"sounds/games/snake/intro.wav", 0.5f)
    private val helpImg = CPArrayImage(
        prepSeq(
            """
              | ______     __   __     ______     __  __     ______
              |/\  ___\   /\ "-.\ \   /\  __ \   /\ \/ /    /\  ___\
              |\ \___  \  \ \ \-.  \  \ \  __ \  \ \  _"-.  \ \  __\
              | \/\_____\  \ \_\\"\_\  \ \_\ \_\  \ \_\ \_\  \ \_____\
              |  \/_____/   \/_/ \/_/   \/_/\/_/   \/_/\/_/   \/_____/
              |
              |
              |
              |       >> BEWARE OF INITIAL KEYBOARD PRESS DELAY <<
              |      >> CHANGE DIFFICULTY BY RESIZING THE SCREEN <<
              |
              |                 ____ ____ ____ ____
              |                ||w |||a |||s |||d ||
              |                ||__|||__|||__|||__||
              |                |/__\|/__\|/__\|/__\|
              |
              |                    [ENTER]   Play
              |                    [Q]       Quit
              |
              |
              |                                            
              |            Copyright (C) 2022 Rowan Games, Inc
            """),
        (ch, _, y) =>
            if y == 21 then ch&C3
            else
                ch match
                    case c if c.isLetter => c&C4
                    case '<' | '>' => ch&C2
                    case _ => ch.toUpper&C1
    ).trimBg()

    // Add scene objects...
    addObjects(
        CPImageSprite(xf = c => (c.w - helpImg.w) / 2, c => (c.h - helpImg.h) / 2, 0, helpImg),
        new CPOffScreenSprite(shaders = Seq(CPFadeInShader(true, 2000, BG_PX))),
        CPKeyboardSprite(KEY_LO_Q, _.exitGame()), // Exit on 'Q' press.
        CPKeyboardSprite(KEY_ENTER, _.switchScene("play"))// Transition to the next scene on 'Enter' press.
    )

    override def onActivate(): Unit =
        super.onActivate()
        introSnd.loopAll(2000) // Start background audio.

    override def onDeactivate(): Unit =
        super.onDeactivate()
        introSnd.stop(400) // Stop background audio.
