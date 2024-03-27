package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.getFilmStorage().createFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return filmService.getFilmStorage().getFilms();
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.getFilmStorage().updateFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmService.getFilmStorage().getFilmById(id);
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
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count);
    }

    @GetMapping("/genres")
    public Collection<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("genres/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        return filmService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public Collection<RatingMPA> getMPA() {
        return filmService.getMPA();
    }

    @GetMapping("mpa/{id}")
    public RatingMPA getMPAById(@PathVariable Integer id) {
        return filmService.getMPAById(id);
    }
}
