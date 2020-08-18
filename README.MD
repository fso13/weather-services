1.  Собрать проект

```
mvn clean install
```

2.  Запустить

```
java -jar target\weather-services-1.0.0-SNAPSHOT.jar
```

3.  Иницилизация

Существует 5 юзеров:   
     
*   user1(1111)
*   user2(2222)
*   user3(3333)
*   user4(4444)
*   admin(password)

4.  Примеры запросов

В запросах используется Basic Auth.

Получение погоды по названию города:

    GET localhost:8191/weather?city=Москва

Пример ответа:

```
{
    "city": "Moscow",
    "temperature": 3,
    "wind": "5SW"
}
```

Получение погоды по координатам:

    localhost:8191/weather?lon=45&lat=67

Пример ответа:
```
{
    "city": "Kamenka",
    "temperature": -12,
    "wind": "1W"
}
```
