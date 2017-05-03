/*
 *  NestedBird  Copyright (C) 2016-2017  Michael Haddon
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License version 3
 *  as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nestedbird.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * This class is responsible for configuring the threads
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
public class ThreadConfig {
    /**
     * Create TaskExecutor bean
     *
     * @return the task executor
     */
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
    //
    //    @Bean
    //    public CommandLineRunner schedulingRunner(final TaskExecutor executor) {
    //        return new CommandLineRunner() {
    //            public void run(String... args) throws Exception {
    //                executor.execute(new SimularProfesor());
    //            }
    //        };
    //    }
}
