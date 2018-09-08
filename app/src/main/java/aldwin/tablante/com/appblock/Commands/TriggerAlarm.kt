package aldwin.tablante.com.appblock.Commands

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri

class TriggerAlarm {

     fun playAlarm(context: Context) {
        var r : Ringtone
        var alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if(alarmUri == null){
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        }

        r = RingtoneManager.getRingtone(context.applicationContext,alarmUri)
        r.play()
    }
}