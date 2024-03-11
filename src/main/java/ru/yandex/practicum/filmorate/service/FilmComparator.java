package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparator implements Comparator<Film> {

    @Override
    public int compare(Film film1, Film film2) {
        if (film1.getLikes() == null && film2.getLikes() == null) {
            return film1.getId().compareTo(film2.getId());
        } else if (film1.getLikes() == null) {
            return -1;
        } else if (film2.getLikes() == null) {
            return 1;
        } else {
            return Integer.compare(film2.getLikes().size(), film1.getLikes().size());
        }
    }
}
