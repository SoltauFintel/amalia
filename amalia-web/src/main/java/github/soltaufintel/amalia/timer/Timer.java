package github.soltaufintel.amalia.timer;

import java.util.TimeZone;

import org.pmw.tinylog.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import github.soltaufintel.amalia.web.config.AppConfig;

/**
 * Timer builder
 * 
 * <p>Call Timer.create(config), set TIMER_ACTIVE and TIMER_ACTIVE_LABEL at program start.</p>
 * <p>Start an AbtractTimer based class by calling Timer.INSTANCE.createTimer(AbtractTimer based class, built-in default cron expression)</p>
 */
public class Timer {
    /** Set value at program start! */
    public static String TIMER_ACTIVE = "1";
    /** Set value at program start! */
    public static String TIMER_ACTIVE_LABEL = "TIMER_ACTIVE";
    public static Timer INSTANCE;
    private final AppConfig config;
    private final org.quartz.Scheduler scheduler;
    
    public static Timer create(AppConfig config) {
        if (INSTANCE != null) {
            throw new RuntimeException("Timer has already been created!");
        }
        INSTANCE = new Timer(config);
        return INSTANCE;
    }
    
    private Timer(AppConfig config) {
        this.config = config;
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @param timerClass AbstractTimer based class
     * @param defaultCron get cron expression from config using key class name + ".cron". If not present use this default cron expression.
     * <p>Examples:
     * <br>"0 40 23 ? * *"      23:40 o'clock at every day
     * <br>"0 0 8 ? * MON-FRI"   8:00 o'clock from Monday to Friday
     * <br>"0 0 6 1 * ?"         6:00 o'clock at first day of month</p>
     */
    public void createTimer(Class<? extends AbstractTimer> timerClass, String defaultCron) {
        createTimer(timerClass, defaultCron, false);
    }
    
    /**
     * @param timerClass AbstractTimer based class
     * @param defaultCron get cron expression from config using key class name + ".cron". If not present use this default cron expression.
     * <p>Examples:
     * <br>"0 40 23 ? * *"      23:40 o'clock at every day
     * <br>"0 0 8 ? * MON-FRI"   8:00 o'clock from Monday to Friday
     * <br>"0 0 6 1 * ?"         6:00 o'clock at first day of month</p>
     * @param forceTimerAndDefaultCron true: always use given defaultCron und always start the timer,<br>
     * false: timer is not started if TIMER_ACTIVE is not "1", cron expression may be overriden by above mentioned config option.
     */
    public void createTimer(Class<? extends AbstractTimer> timerClass, String defaultCron, boolean forceTimerAndDefaultCron) {
        if (forceTimerAndDefaultCron) {
            installTimer(timerClass, defaultCron);
        } else if (checkIfTimersAreActive(timerClass)) {
            String cron = config.get(timerClass.getSimpleName() + ".cron", defaultCron);
            installTimer(timerClass, cron);
        }
    }
    
    private void installTimer(Class<? extends AbstractTimer> timerClass, String cron) {
        if (isNullOrEmpty(cron) || "-".equals(cron.trim())) {
            Logger.debug("Timer " + timerClass.getSimpleName() + " has not been started because cron expression is empty or '-'.");
        } else {
            try {
                JobDetail job = JobBuilder.newJob(timerClass).build();
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withSchedule(CronScheduleBuilder.cronSchedule(cron).inTimeZone(TimeZone.getTimeZone("CET"))).build();
                scheduler.scheduleJob(job, trigger);
                Logger.info(timerClass.getSimpleName() + " started. cron: " + cron);
            } catch (Exception e) {
                throw new RuntimeException("Error scheduling timer " + timerClass.getSimpleName() + " with cron \"" + cron + "\"", e);
            }
        }
    }
    
    public static boolean checkIfTimersAreActive(Class<? extends AbstractTimer> timerClass) {
        boolean active = "1".equals(TIMER_ACTIVE);
        if (!active) {
            Logger.debug("Timer " + timerClass.getSimpleName() + " has not been started because '" + TIMER_ACTIVE_LABEL + "' is not '1'.");
        }
        return active;
    }
    
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isBlank();
    }
}
