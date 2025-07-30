package wtf.n1zamu.util;

import org.bukkit.Bukkit;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public class TimeUtil {
    private static int questReloadHour = ConfigUtil.getInt("questReloadHour");
    private static int questReloadMinute = ConfigUtil.getInt("questReloadMinute");

    public static long calculateTimeToSaturday() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));

        ZonedDateTime nextSaturday = now.withHour(questReloadHour).withMinute(questReloadMinute).withSecond(0).withNano(0);

        if (now.getDayOfWeek().getValue() == 6 && now.isAfter(nextSaturday)) {
            nextSaturday = nextSaturday.plusWeeks(1);
        } else if (now.getDayOfWeek().getValue() < 6) {
            nextSaturday = nextSaturday.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        } else if (now.getDayOfWeek().getValue() > 6) {
            nextSaturday = nextSaturday.plusWeeks(1);
        }

        long secondsUntilNextSaturday = Duration.between(now, nextSaturday).getSeconds();
        Bukkit.getLogger().info("До обновления еженедельных квестов: " + secondsUntilNextSaturday + " секунд!");
        return secondsUntilNextSaturday;
    }

    public static long calculateTimeToNextDay() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        ZonedDateTime nextUpdate;
        if (now.getHour() < questReloadHour) {
            nextUpdate = now.withHour(questReloadHour).withMinute(questReloadMinute).withSecond(0).withNano(0);
        } else {
            nextUpdate = now.plusDays(1).withHour(questReloadHour).withMinute(questReloadMinute).withSecond(0).withNano(0);
        }
        long secondsUntilNextDayAtSixAM = Duration.between(now, nextUpdate).getSeconds();
        Bukkit.getLogger().info("До обновления ежедневных квестов: " + secondsUntilNextDayAtSixAM + " секунд!");

        return secondsUntilNextDayAtSixAM;
    }
}
