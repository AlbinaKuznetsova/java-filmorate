package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static Integer count = 0;

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        if (film.getId() == null) {
            film.setId(generateId());
        }
        films.put(film.getId(), film);
        log.info("Создан фильм {}", film);
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен фильм {}", film);
        } else {
            log.warn("Фильма с id = {} не существует", film.getId());
            throw new ObjectNotFoundException("Фильма с id = " + film.getId() + " не существует");
        }
        return film;
    }

    @Override
    public Film getFilmById(Integer id) {
        if (films.get(id) == null) {
            throw new ObjectNotFoundException("Фильма с id = " + id + " не существует");
        }
        return films.get(id);
    }

    private Integer generateId() {
        return ++count;
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.warn("Название не может быть пустым\n" + film);
            throw new ValidationException("Название не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.warn("Описание слишком длинное\n" + film);
            throw new ValidationException("Описание слишком длинное");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Фильм слишком старый\n" + film);
            throw new ValidationException("Фильм слишком старый");
        } else if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительной\n" + film);
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
