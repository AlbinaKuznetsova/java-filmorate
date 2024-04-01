package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private RatingMPA mpa;
    private Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
}
