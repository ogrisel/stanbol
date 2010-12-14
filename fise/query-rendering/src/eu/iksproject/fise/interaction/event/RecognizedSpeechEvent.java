package eu.iksproject.fise.interaction.event;

/*
 * Copyright 2010
 * German Research Center for Artificial Intelligence (DFKI)
 * Department of Intelligent User Interfaces
 * Germany
 *
 *     http://www.dfki.de/web/forschung/iui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Sebastian Germesin
 *     Massimo Romanelli
 *     Tilman Becker
 */

public class RecognizedSpeechEvent extends Event {

    private String recognizedSpeech;

    public RecognizedSpeechEvent (String recognizedSpeech) {
        this.recognizedSpeech = recognizedSpeech;
    }

    public String getData() {
        return recognizedSpeech;
    }

}
