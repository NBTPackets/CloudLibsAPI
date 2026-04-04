<p align="center">
  # CloudLibsAPI
  <br><br>
  <img src="https://img.shields.io/badge/Minecraft-1.20–1.21+-orange" alt="Supported versions"/>
  <img src="https://img.shields.io/badge/Platform-Paper%20%7C%20Spigot-blue" alt="Platform"/>
  <img src="https://img.shields.io/badge/Java-17%2B-red" alt="Java"/>
  <a href="https://t.me/Vladimir_Vladimirovich_Putln">
    <img src="https://img.shields.io/badge/Telegram-NBTPackets-2CA5E0?logo=telegram&logoColor=white"/>
  </a>
</p>

>### Что может эта библиотека:
>
> - Поддержка цветов через TextColor (минимеседж)
> - Удобная система логов через SLF4J
> - Работа с игроками (отправка сообщения, работа с инв, получения ника и UUID игрока и тд...)
> - Удобная работа с Scheduler
> - Готовая система меню

> **CloudLibsAPI** — это просто сборник повторяющегося кода, который мне надоело писать по сто раз

# Подключение
### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

```
```xml
<dependency>
    <groupId>com.github.NBTPackets</groupId>
    <artifactId>CloudLibsAPI</artifactId>
    <version>VERSION</version> <!-- укажите актуальную версию -->
    <scope>provided</scope>
</dependency>
```

### Gradle
```groovy
dependencyResolutionManagement { 
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation 'github.com.nbtpackets:CloudLibsAPI:VERSION' // укажите актуальную версию
}
```

### plugin.yml
```yaml
depend: [CloudLibsAPI]
```

# Как использовать?

### Цвета:
```java
// отправляет сообщение с цветом игроку
player.sendMessage(TextColor.parse("<red>текст"));
```
или же сообщение от либы где сразу есть поддержка цветов 
```java
PlayerMsg.send(player, "<green>текст");
```
### Логирование:
```java
public class Plugin extends JavaPlugin {

    private LogUtil log; // добавьте это поле

    @Override
    public void onEnable() {
        log = LogUtil.of(this); // добавьте это

        // логи
        log.info("инфо");
        log.warn("варн {}", message);
        log.error("ошибка {}", error);
    }
}
```

В других классах:
```java
public class Test {
    
    private final LogUtil log; // добавьте это

    // и это
    public Test(LogUtil log) { 
        this.log = log;
    }

    public void test() {
        // лог
        log.info("текст");
    }
}
```

### Работа с игроками

### PlayerFind — Поиск и состояние
```java
// проверка по нику
PlayerFind.name("NBTPackets").ifPresent(player -> {
    player.sendMessage("да ты крут");
});

// проверка онлайн ли игрок (код внутри {} выполнится ТОЛЬКО если игрок онлайн)
if (PlayerFind.isOnline("NBTPackets")) { ... }

// получить всех игроков
PlayerFind.all().forEach(p -> ...);
```

### Жизнь и еда
```java
// полностью восстановить (хп + еда + убрать эффектыи огонь)
PlayerLife.restore(player);

// Просто похилить
PlayerLife.heal(player);

// Покормить
PlayerLife.feed(player);

// Сменить режим игры
PlayerLife.gm(player, GameMode.CREATIVE);
```

### Инвентарь
```java
// очистить всё
PlayerInv.clear(player);

// выдать предмет (если нет места выкинет рядом)
PlayerInv.give(player, itemStack);

// проверка есть ли место в инв
if (PlayerInv.hasSpace(player)) { ... }
```

### Сообщения
```java
// Ссообщение в чат (сразу с цветами)
PlayerMsg.send(player, "<green>текст");

// экшнбар
PlayerMsg.action(player, "<green>текст");

// тайтл
PlayerMsg.title(player, "<green>текст", "<green>текст");
```

#### Благодаря PlayerFind можно например
```java
// аолучаем всех игроков
PlayerFind.all().stream()
    // оставляем только тех, у кого есть право
    .filter(player -> player.hasPermission("perm.tox"))
        // проверяем есть ли у игрока место в инвентаре
        .filter(PlayerInv::hasSpace)
    // действия для каждого прошедшего игрока
    .forEach(player -> {
        // выдаем алмаз (если места нет то упадет под ноги)
        PlayerInv.give(player, new ItemStack(Material.DIAMOND));
        // сообщение
        PlayerMsg.send(player, "<green>вы получили <bold>алмаз</bold>");
    });
```

#### Пример экшен бара всем игрокам
```java
PlayerFind.all().forEach(player -> PlayerMsg.action(p, "текст"));
```

### Удобная работа с Scheduler
Синхронные задачи
```java
// выполнить сразу
Task.sync(() -> {
    PlayerMsg.send(player, "просто пример");
});
```
```java 
// выполнить один раз с задержкой (в тиках)
Task.later(20L, () -> {
    PlayerMsg.send(player, "1 сек");
});
```
```java 
// повторяющаяся задача
Task.timer(0L, 20L, () -> {
    PlayerMsg.send(player, "повторяеться");
});
```

#### Асинхронные задачи
```java
// выполнить асинхронно
Task.async(() -> {
    loadData();
});
```
```java
// асинхронно с задержкой
Task.laterAsync(40L, () -> {
    heavy();
});
```
```java
// асинхронный таймер
Task.timerAsync(0L, 100L, () -> {
    update();
});
```

#### Таймеры с управлением

Эти методы передают BukkitTask внутрь
что позволяет остановить таймер из самой задачи

Синхронный таймер
```java
Task.timerSelf(0L, 20L, task -> {
    if (seconds-- <= 0) {
        task.cancel(); // остановка таймера
        PlayerMsg.send(player, "завершён");
    }
});
```
Асинхронный таймер
```java
Task.timerAsyncSelf(0L, 20L, task -> {
    if (!running) {
        task.cancel();
    }
});
```
#### Система меню

Больше никаких _**чем гуще лес if else if else**_

Предметом без обновления данных
```java
public class Menu extends BaseMenu {

    public MyMenu(Player player) {
        super(27, Component.text("Меню"));

        // кнопка с действием
        setItem(10, 
            ItemBuilder.create(Material.DIAMOND)
                .name("<gold>Алмаз")
                .lore("<gray>Нажми чтобы получить")
                .build(),
            event -> {
                PlayerMsg.send(event.player(), "<green>Ты получил алмаз!");
                player.getInventory().addItem(new ItemStack(Material.DIAMOND));
                event.playSound(Sound.ENTITY_PLAYER_LEVELUP);
            }
        );
```
Предмет с обновлением данных
```java
public class UpdateMenu extends BaseMenu {

    public BalanceMenu(Player player) {
        super(27, Component.text("Баланс"));

        // предмет который сам обновляется
        setDynamicItem(13, () -> 
            ItemBuilder.create(Material.GOLD_INGOT)
                .name("<yellow>Ваш составляет: <gold>" + getBalance(player))
                .build(),
            event -> {
                addMoney(player, 100);
                refresh(); // обновляем меню
            }
        );

        // автообновление каждую секунду
        startAutoUpdate(20L);
    }
```
Открытие меню
```java
MenuFactory.open(player, () -> new MyMenu(player));
```
Как делает _ГЕНИЙ_:
```java
if (title.equals("Меню")) {
if (slot == 10) {
if (item.getType() == Material.DIAMOND) {
// логика
}
} else if (slot == 11) {
// еще логика
} // и так 50 раз... как долбаеб
}
```
Как можно делать:
```java
setItem(10, item, event -> {
// логика прямо здесь
});
```
Теперь вы можете делать все рядом, а не через хуйню