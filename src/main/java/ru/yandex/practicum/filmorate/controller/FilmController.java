package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
public class FilmController {
    private FilmStorage filmStorage;
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
        this.filmStorage = filmService.getFilmStorage();
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        return filmStorage.createFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        return filmStorage.updateFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmStorage.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void createLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.createLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count);
    }
}
