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

package org.cosplay

/*
   _________            ______________
   __  ____/_______________  __ \__  /_____ _____  __
   _  /    _  __ \_  ___/_  /_/ /_  /_  __ `/_  / / /
   / /___  / /_/ /(__  )_  ____/_  / / /_/ /_  /_/ /
   \____/  \____//____/ /_/     /_/  \__,_/ _\__, /
                                            /____/

          2D ASCII GAME ENGINE FOR SCALA3
            (C) 2021 Rowan Games, Inc.
               ALl rights reserved.
*/

/**
  * Container for rendering statistics.
  *
  * @param frameCount Current frame number since the beginning of the game.
  * @param sceneFrameCount Current frame number since the start of the current scene.
  * @param fps Current frames-per-second (FPS) rate.
  * @param avgFps Average frames-per-second (FPS) over the last 500 frames.
  * @param low1PctFps Average frames-per-second (FPS) value across lowest 1% of all FPS values for the last 500 frames.
  * @param userTimeNs Time in nanoseconds spent in user logic for the current frame.
  * @param sysTimeNs Average time in nanoseconds spent by the CosPlay engine for the current frame.
  * @param objCount Total count of scene objects (including visible and invisible ones).
  * @param visObjCount Total count of visible scene objects.
  * @param kbEvent Keyboard event, if any, for the current frame.
  *                
  * @see [[CPEngine.addStatsListener()]]
  * @see [[CPEngine.removeStatsListener()]]
  */
case class CPRenderStats(
    frameCount: Long,
    sceneFrameCount: Long,
    fps: Int,
    avgFps: Int,
    low1PctFps: Int,
    userTimeNs: Long,
    sysTimeNs: Long,
    objCount: Long,
    visObjCount: Long,
    kbEvent: Option[CPKeyboardEvent]
)
