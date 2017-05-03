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

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SnappyCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is responsible for configuring redis
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
public class RedisConfig {

    /**
     * the hostname of the redis server
     */
    private final String redisHostName;

    /**
     * the port of the redis server
     */
    private final Integer redisPort;

    /**
     * the password of the redis server
     */
    private final String redisPassword;

    /**
     * Instantiates a new Redis config.
     *
     * @param redisHostName the redis host name
     * @param redisPort     the redis port
     * @param redisPassword the redis password
     */
    public RedisConfig(@Value("${spring.redis.host}") final String redisHostName,
                       @Value("${spring.redis.port}") final Integer redisPort,
                       @Value("${spring.redis.password}") final String redisPassword) {
        this.redisHostName = redisHostName;
        this.redisPort = redisPort;
        this.redisPassword = redisPassword;
    }

    /**
     * Redisson client bean
     *
     * @return the redisson client
     */
    @Bean
    public RedissonClient redissonClient() {
        return Redisson.create(redissonConfig());
    }

    /**
     * Creating the redisson configuration object
     *
     * @return the redisson config
     */
    @Bean
    public Config redissonConfig() {
        final Config config = new Config()
                .setCodec(new SnappyCodec());

        config.useSingleServer()
                .setAddress(redisHostName + ":" + redisPort)
                .setPassword(redisPassword)
                .setRetryAttempts(3)
                .setTimeout(1000)
                .setConnectionPoolSize(500)
                .setRetryInterval(2000);
        return config;
    }
}
