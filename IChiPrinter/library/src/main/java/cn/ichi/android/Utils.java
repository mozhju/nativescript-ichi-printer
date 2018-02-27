package cn.ichi.android;

import android.app.Application;

import java.lang.reflect.Method;

/**
 * Created by mozj on 2018/2/26.
 */

public class Utils {

    private static Application m_Application = null;

    public static Application getApplication()  {
        if (m_Application == null) {
            try {
                Class<?> mClass = Class.forName("android.app.ActivityThread");
                Method currentActivityThread = mClass.getMethod("currentActivityThread");
                Method getApplication = mClass.getMethod("getApplication");

                Object activityThread = currentActivityThread.invoke(null);

                m_Application = (Application) getApplication.invoke(activityThread);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return m_Application;
    }
}
