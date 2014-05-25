hibernate-redis-cache
=====================

Hibernate 2nd level cache using redis


	Hibernate (3.7.10.Final)
	Jedis 2.4.2

## Features

* Implement "read-only" and "non strict read-write" strategies


## Build

To build:

```
$ git clone git@github.com:gerulrich/hibernate-redis-cache.git
$ cd hibernate-redis-cache
$ mvn install
```

Maven:

```xml
<dependency>
	<groupId>com.github.gerulrich</groupId>
	<artifactId>hibernate-redis-cache</artifactId>
	<version>0.1</version>
</dependency>
```

Hibernate configuration

```xml
<property name="hibernate.cache.region.factory_class">com.github.gerulrich.hibernate.SpringProxyRegionFactory</property>
```

Spring configuration

```xml

<!-- configuration for JedisPool -->
<bean id="jedisPool" class="redis.clients.jedis.JedisPool">
	<constructor-arg index="0" value="${redis.host}" />
	<constructor-arg index="1" value="${redis.port}" />
</bean>

<!-- inject in the proxy the real region factory (managed by spring) -->
<bean id="springProxyRegionFactory" class="com.github.gerulrich.hibernate.SpringProxyRegionFactory">
	<property name="regionFactory" ref="redisRegionFactory"/>
</bean>

<bean id="redisRegionFactory" class="com.github.gerulrich.redis.RedisCacheRegionFactory">
	<property name="jedisPool" ref="jedisPool"/>
	<!-- default Key Generator for redis keys -->
	<property name="keyGenerator" value="com.github.gerulrich.redis.cache.key.ToStringKeyGenerator"/>
	<!-- default serializer to store objects on redis -->
    <property name="serializer" value="com.github.gerulrich.redis.cache.serializer.StandarSerializer"/>
	<!-- default ttl (expiration time for redis) -->
	<property name="ttl" value="120"/>
	<!-- preffix for all generated keys -->
	<property name="preffix" value="myapp:test"/>
	<property name="properties">
		<props>
			<!-- override defaults -->
			<prop name="com.myapp.MyEntity.key_generator">com.myapp.key.generator.CustomKeyGenerator</prop>
			<prop name="com.myapp.MyEntity.serializer">com.myapp.serializer.CustomSerializer</prop>
			<prop name="com.myapp.MyEntity.ttl">300</prop>
		</props>
	</property>
</bean>

<!-- EntityManagerFactory de JPA -->
<bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean" depends-on="springProxyRegionFactory,redisRegionFactory">
	// ..
</bean>
```


## LICENSE

Copyright 2014 German Ulrich

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.