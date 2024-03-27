package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.dao.FilmServiceDao;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserStorage userStorage;
    private final FilmServiceDao filmServiceDao;

    public void createLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId); // Проверяем, что пользователь существует, иначе выбросится исключение
        filmServiceDao.createLike(filmId, userId);
        log.info("Фильму id = " + filmId + "поставил лайк пользователь id = " + userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        filmServiceDao.deleteLike(filmId, userId);
        log.info("Удален лайк filmId = " + filmId + ", userId = " + userId);
    }

    public List<Film> getPopular(Integer count) {
        FilmComparator filmComparator = new FilmComparator();
        return filmStorage.getFilms().stream().sorted(filmComparator)
                .limit(count).collect(Collectors.toList());
    }

    public Collection<Genre> getGenres() {
        return filmServiceDao.getGenres();
    }

    public Genre getGenreById(Integer id) {
        return filmServiceDao.getGenreById(id);
    }

    public Collection<RatingMPA> getMPA() {
        return filmServiceDao.getMPA();
    }

    public RatingMPA getMPAById(Integer id) {
        return filmServiceDao.getMPAById(id);
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }
}
