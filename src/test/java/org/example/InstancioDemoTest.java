package org.example;

import lombok.extern.slf4j.Slf4j;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Select.types;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class InstancioDemoTest {

    @Test
    @DisplayName("Простое создание объекта")
    void simpleCreateTest() {
        User user = Instancio.create(User.class);
        assertNotNull(user);
        assertNotNull(user.getName());
        assertNotNull(user.getAddress());

        log.info("user: {}", user);
    }

    @Test
    @DisplayName("Установка значения в поле")
    void setFieldValueTest() {
        User user = Instancio.of(User.class)
                .set(field(User::getEmail), "test@mail.ru")
                .create();
        assertNotNull(user);
        assertEquals("test@mail.ru", user.getEmail());
    }

    @Test
    @DisplayName("Генерация значения в поле")
    void genFieldValueTest() {
        User user = Instancio.of(User.class)
                .generate(field(User::getEmail), gen -> gen.net().email())
                .generate(field(User::getPassword), gen -> gen.string()
                        .mixedCase()
                        .maxLength(10))
                .generate(field(User::getName), gen -> gen.text().word())
                .generate(field(User::getKeyWord), gen -> gen.text().uuid())
                .create();
        assertNotNull(user);

        assertTrue(user.getEmail().contains("@"));
        assertTrue(user.getPassword().length() <= 10);

        log.info("email = {}", user.getEmail());
        log.info("password = {}", user.getPassword());
        log.info("name = {}", user.getName());
        log.info("keyWord = {}", user.getKeyWord());
    }

    @Test
    @DisplayName("Селектор по типу")
    void selectTypeTest() {
        User user = Instancio.of(User.class)
                .generate(types().of(String.class), gen -> gen.string()
                        .upperCase()
                        .length(15))
                .generate(types().of(List.class), gen -> gen.collection().maxSize(5))
                .create();
        assertNotNull(user);

        assertTrue(user.getPassword().length() == 15);
        assertTrue(user.getAddress().getCity().length() == 15);
        assertTrue(user.getStatisticLoginsList().size() <= 5);

        log.info("password = {}", user.getPassword());
        log.info("city = {}", user.getAddress().getCity());
        log.info("statisticLoginsList = {}", user.getStatisticLoginsList().size());
    }

    @Test
    @DisplayName("Игнор по полю")
    void ignoreFieldTest() {
        User user = Instancio.of(User.class)
                .ignore(all(
                        field(User::getId),
                        field(User::getStatisticLoginsList)
                ))
                .create();
        assertNotNull(user);

        assertNull(user.getId());
        assertNull(user.getStatisticLoginsList());
    }

    @Test
    @DisplayName("Генерация списка объектов")
    void genCollectionUsersTest() {
        var users = Instancio.ofList(User.class)
                .size(10)
                .create();
        assertEquals(10, users.size());
    }

    @Test
    @DisplayName("Генерация по модели")
    void modelTest() {
        var userModel = Instancio.of(User.class)
                .generate(field(User::getEmail), gen -> gen.net().email())
                .generate(field(User::getPassword), gen -> gen.string()
                        .mixedCase()
                        .maxLength(10))
                .generate(field(User::getName), gen -> gen.text().word())
                .generate(field(User::getKeyWord), gen -> gen.text().uuid())
                .toModel();
        var user = Instancio.create(userModel);
        assertNotNull(user);

        assertTrue(user.getEmail().contains("@"));
        assertTrue(user.getPassword().length() <= 10);
    }

    @Test
    @DisplayName("Генерация по условию")
    void assignTest() {
        var userModel = Instancio.of(User.class)
                .generate(field(User::getName), gen -> gen.oneOf("Mark", "Alex", "Vlad"))
                .assign(Assign.given(User::getName).is("Mark").set(field(User::getAge), 2))
                .toModel();
        var users = Instancio.ofList(userModel).size(3)
                .withUnique(field(User::getName))
                .create();

        users.forEach(user -> {
            assertNotNull(user.getAge());
            if (user.getName().equals("Mark")) {
                assertEquals(2, user.getAge());
            }

            log.info("User = {}; age = {}", user.getName(), user.getAge());
        });
    }

}
