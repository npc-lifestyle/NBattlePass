package wtf.n1zamu.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

@UtilityClass
public class TimeUtil {
    private static final int RELOAD_HOUR;
    private static final int RELOAD_MINUTE;

    static {
        RELOAD_HOUR = ConfigUtility.getInt("questReloadHour");
        RELOAD_MINUTE = ConfigUtility.getInt("questReloadMinute");
    }

    public long calculateTimeToSaturday() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));

        ZonedDateTime nextSaturday = now.withHour(RELOAD_HOUR).withMinute(RELOAD_MINUTE).withSecond(0).withNano(0);

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

    public long calculateTimeToNextDay() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        ZonedDateTime nextUpdate;
        if (now.getHour() < RELOAD_HOUR) {
            nextUpdate = now.withHour(RELOAD_HOUR).withMinute(RELOAD_MINUTE).withSecond(0).withNano(0);
        } else {
            nextUpdate = now.plusDays(1).withHour(RELOAD_HOUR).withMinute(RELOAD_MINUTE).withSecond(0).withNano(0);
        }
        long secondsUntilNextDayAtSixAM = Duration.between(now, nextUpdate).getSeconds();
        Bukkit.getLogger().info("До обновления ежедневных квестов: " + secondsUntilNextDayAtSixAM + " секунд!");

        return secondsUntilNextDayAtSixAM;
    }
}
