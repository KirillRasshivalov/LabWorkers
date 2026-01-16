package algo.demo.dto;

import java.util.List;

public record StatisticInfo(Integer maxHoursTeaching, String maxHoursTeacher, Integer weight, String theLightestPearson,
                            List<String> placesThereTeachersTeach) {}
