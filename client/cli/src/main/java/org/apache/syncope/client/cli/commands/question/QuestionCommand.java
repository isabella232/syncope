/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.client.cli.commands.question;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.client.cli.Command;
import org.apache.syncope.client.cli.Input;
import org.apache.syncope.client.cli.commands.AbstractCommand;

@Command(name = "question")
public class QuestionCommand extends AbstractCommand {

    private static final String HELP_MESSAGE = "Usage: question [options]\n"
            + "  Options:\n"
            + "    --help \n"
            + "    --list \n"
            + "    --read \n"
            + "       Syntax: --read {QUESTION-ID} {QUESTION-ID} [...]\n"
            + "    --delete \n"
            + "       Syntax: --delete {QUESTION-ID} {QUESTION-ID} [...]";

    @Override
    public void execute(final Input input) {
        if (StringUtils.isBlank(input.getOption())) {
            input.setOption(QuestionOptions.HELP.getOptionName());
        }

        switch (QuestionOptions.fromName(input.getOption())) {
            case LIST:
                new QuestionList(input).list();
                break;
            case READ:
                new QuestionRead(input).read();
                break;
            case DELETE:
                break;
            case HELP:
                System.out.println(HELP_MESSAGE);
                break;
            default:
                new QuestionResultManager().defaultError(input.getOption(), HELP_MESSAGE);
        }
    }

    @Override
    public String getHelpMessage() {
        return HELP_MESSAGE;
    }

    private enum QuestionOptions {

        HELP("--help"),
        LIST("--list"),
        READ("--read"),
        DELETE("--delete");

        private final String optionName;

        QuestionOptions(final String optionName) {
            this.optionName = optionName;
        }

        public String getOptionName() {
            return optionName;
        }

        public boolean equalsOptionName(final String otherName) {
            return (otherName == null) ? false : optionName.equals(otherName);
        }

        public static QuestionOptions fromName(final String name) {
            QuestionOptions optionToReturn = HELP;
            for (final QuestionOptions option : QuestionOptions.values()) {
                if (option.equalsOptionName(name)) {
                    optionToReturn = option;
                }
            }
            return optionToReturn;
        }

        public static List<String> toList() {
            final List<String> options = new ArrayList<>();
            for (final QuestionOptions value : values()) {
                options.add(value.getOptionName());
            }
            return options;
        }
    }
}
