package ru.practicum.mapper;

import ru.practicum.ViewStatsDto;
import ru.practicum.model.ViewStats;

import java.util.ArrayList;
import java.util.List;

public class ViewStatsMapper {
    public static List<ViewStatsDto> toDtos(List<ViewStats> stats) {
        List<ViewStatsDto> res = new ArrayList<>();
        for (ViewStats stat : stats) {
            res.add(new ViewStatsDto(
                    stat.getApp(),
                    stat.getUri(),
                    stat.getHits())

            );
        }
        return res;
    }

    public static ViewStats to(ViewStatsDto stats) {
        return new ViewStats(
                stats.getApp(),
                stats.getUri(),
                stats.getHits()
        );
    }
}
