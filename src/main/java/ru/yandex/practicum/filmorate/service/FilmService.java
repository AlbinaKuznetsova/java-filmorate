package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void createLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId); // Проверяем, что пользователь существует, иначе выбросится исключение
        film.getLikes().add(userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(Integer count) {
        FilmComparator filmComparator = new FilmComparator();
        return filmStorage.getFilms().stream().sorted(filmComparator)
                .limit(count).collect(Collectors.toList());
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }
}
