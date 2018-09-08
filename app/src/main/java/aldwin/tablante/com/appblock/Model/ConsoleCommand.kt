package aldwin.tablante.com.appblock.Model

class ConsoleCommand(BootDevice:Boolean,Screenshot:Boolean,CaptureCam:Boolean,TriggerAlarm:Boolean ,
                     Messages:String,Applications:ArrayList<String>,AppPermit:Boolean,KillApp:String) {

    var BootDevice = BootDevice
    var Screenshot = Screenshot
    var CaptureCam =CaptureCam
    var TriggerAlarm = TriggerAlarm
    var Messages = Messages
    var Applications = Applications
    var AppPermit = AppPermit
    var KillApp= KillApp

    constructor() : this(false,false,false,false,
            "",ArrayList(),false,"")
}