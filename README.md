# Sferum-test-task-2022
Тестовое задание для направления бэкэнд для Сферум 2022. <br/>
Техзадание: https://like-brace-188.notion.site/dc79d7bc33cc477bb3e7ba6e40c62f3f
Зеркало: https://disk.yandex.ru/d/n20PI-RSyGU0HA

# Features
Создается база данных, содержащая список пользователей и каталог книг. При запуске требуется аунтефикация пользователя. 
Далее текущий пользователь получает доступ к своим данным и балансу.

# Commands list
```print_balance``` - вывести баланс пользоваетля. <br/>
```show_books_in_stock``` - показать все книги в наличии. <br/>
```buy "<name>" <quantity>``` - купить книгу. <br/>
```show_bought_books``` - показать купленные пользователем книги. <br/>
```top_up <amount>``` - пополнить баланс пользователя. <br/>
```add_books``` - добавить книги в каталог. Не требует авторизации. Эта же команда позволяет изменить цену или добавить уже существующих книг. <br/>
```help``` - вывести список команд. <br/>
```login <username> <password>``` - вой под именем существующего пользователя. <br/>
```register <username> <password> <repeat password> <balance>``` - зарегистрировать пользователя. <br/>
```logout``` - выйти из аккаунта. <br/>
```exit``` - выйти из приложения. <br/>

# Коментарии
Я первый раз писал на Java, но к счастью она похожа на шарпы и я вроде более-менее разобрался. <br/>
Задумка добавления книг без регистрации в том, что каждый сможет публиковать свои учебники, ружналы, книги самостаятелько как только ему это захочется. 
Авторизация не требуется из за отсутствия автораства у книг на данный момент.
