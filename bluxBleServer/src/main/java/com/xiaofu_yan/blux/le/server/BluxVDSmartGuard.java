package com.xiaofu_yan.blux.le.server;

import java.util.UUID;

class BluxVDSmartGuard extends BluxVirtualDevice
{
    Delegate delegate;
    static final UUID VD_TYPE_UUID = UUID.fromString("78667579-3E4B-4BF2-9D52-5FFE97D10C2A")
            ;
    private static final int REG_SMART_GUARD_COMMAND = 17;
    private static final int REG_SMART_GUARD_PLAY_MUSIC = 18;
    private static final int REG_SMART_GUARD_OPEN_TRUNK = 19;
    private static final int REG_SMART_GUARD_ADC = 33;
    private static final int REG_SMART_GUARD_MILEAGE = 34;
    private static final int REG_SMART_GUARD_STATE = 50;
    private static final int REG_SMART_GUARD_SYNC_STATE = 51;
    private static final int REG_SMART_GUARD_AUTO_ARM = 52;
    private static final int REG_SMART_GUARD_ALARM = 53;
    private static final int REG_SMART_GUARD_SPEED_CONFIG = 65;
    private static final int REG_SMART_GUARD_SHOCK_SENSITIVITY = 66;
    private static final int REG_SMART_GUARD_SHOCK_LEVEL_TABLE = 67;
    private static final int REG_SMART_GUARD_ALARM_MUSIC_CONFIG = 68;
    private static final int REG_SMART_GUARD_ALARM_NOTE_TABLE = 69;
    private static final int REG_SMART_GUARD_ALARM_MUSIC_TABLE = 70;
    private final static int REG_SMART_GUARD_SILENCE_MODE                = 0x47;  /*RW*/
    private static final int VD_SMART_GUARD_COMMAND_ARM = 16;
    private static final int VD_SMART_GUARD_COMMAND_SILENT_ARM = 17;
    private static final int VD_SMART_GUARD_COMMAND_DISARM = 18;
    private static final int VD_SMART_GUARD_STATE_ARMED = 16;
    private static final int VD_SMART_GUARD_STATE_STOPPED = 17;
    private static final int VD_SMART_GUARD_STATE_STARTED = 18;
    private static final int VD_SMART_GUARD_STATE_RUNNING = 19;
    private static final int ALARM_SENSITIVITY_LEVEL_MAX = 25;

    BluxVDSmartGuard(BluxServiceVirtualDevice service, BluxVirtualDevice.Descriptor desc)
    {
        super(service, desc);
    }

    protected void terminate()
    {
        this.delegate = null;
        super.terminate();
    }

    void sendCommand(Command command) {
        byte[] data = new byte[1];

        if (command == Command.ARM)
            data[0] = 16;
        else if (command == Command.SILENT_ARM)
            data[0] = 17;
        else if (command == Command.DISARM) {
            data[0] = 18;
        }
        writeRegister(17, data);
    }

    void playMusic(int id) {
        byte[] data = { (byte)id };

        writeRegister(18, data);
    }

    void openTrunk(int period) {
        byte[] data = new byte[2];

        s2le((short)period, data, 0);

        writeRegister(19, data);
    }

    void getADC(byte channel) {
        byte[] data = { channel };

        readRegister(33, data);
    }

    void readState() {
        readRegister(50, null);
    }

    void writeState(State state, boolean silent) {
        byte[] data = new byte[2];

        data[0] = 19;

        if (state == State.STARTED)
            data[0] = 18;
        else if (state == State.STOPPED)
            data[0] = 17;
        else if (state == State.ARMED) {
            data[0] = 16;
        }
        data[1] = (byte)(silent ? 0 : 1);
        writeRegister(50, data);
    }

    void syncState(boolean autoArm) {
        byte[] data = new byte[1];

        data[0] = (byte)(autoArm ? 1 : 0);
        writeRegister(51, data);
    }

    void setAutoArm(boolean autoArm) {
        byte[] data = new byte[1];

        data[0] = (byte)(autoArm ? 1 : 0);
        writeRegister(52, data);
    }

    void readAlarmConfig() {
        readRegister(53, null);
    }

    void writeAlarmConfig(boolean alarmDevice, boolean notifyPhone) {
        byte[] data = new byte[2];

        data[0] = (byte)(alarmDevice ? 1 : 0);
        data[1] = (byte)(notifyPhone ? 1 : 0);
        writeRegister(53, data);
    }

    void readSpeedConfig() {
        readRegister(65, null);
    }

    void writeSpeedConfig(int monitorPeriod, int hallSensorCounter) {
        byte[] data = new byte[2];

        hallSensorCounter = hallSensorCounter <= 0 ? 1 : hallSensorCounter;
        hallSensorCounter = hallSensorCounter > 255 ? 255 : hallSensorCounter;

        monitorPeriod /= 100;
        monitorPeriod = monitorPeriod <= 0 ? 1 : monitorPeriod;
        monitorPeriod = monitorPeriod > 255 ? 255 : monitorPeriod;

        data[0] = (byte)hallSensorCounter;
        data[1] = (byte)(monitorPeriod / 100);
        writeRegister(65, data);
    }

    void readShockSensitivity() {
        readRegister(66, null);
    }

    void writeShockSensitivity(int level) {
        byte[] data = new byte[1];

        level = level < 0 ? 0 : level;
        level = level > ALARM_SENSITIVITY_LEVEL_MAX ? ALARM_SENSITIVITY_LEVEL_MAX : level;

        data[0] = (byte)level;
        writeRegister(66, data);
    }

    void readShockLevelTable() {
        readRegister(67, null);
    }

    void writeShockLevelTable(byte[] table) {
        writeRegister(52, table);
    }

    void readAlarmMusicConfig() {
        readRegister(68, null);
    }

    void readAlarmMusic(int musicID) {
        byte[] data = new byte[1];

        data[0] = (byte)musicID;

        readRegister(70, data);
    }

    void writeAlarmMusic(int musicID, byte[] data) {
        byte[] d = new byte[data.length + 1];

        d[0] = (byte)musicID;
        arrayCopyToArray(d, 1, data, 0, data.length);

        writeRegister(70, d);
    }

    void readAlarmNote(int noteID) {
        byte[] data = new byte[2];

        data[0] = (byte)noteID;
        data[1] = 1;

        readRegister(69, data);
    }

    void writeAlarmNote(int noteID, byte[] data) {
        byte[] d = new byte[data.length + 1];

        d[0] = (byte)noteID;
        arrayCopyToArray(d, 1, data, 0, data.length);

        writeRegister(69, d);
    }

    void writeMileage(long pulses)
    {
        byte[] data = new byte[4];

        l2le((int)pulses, data, 0);

        writeRegister(34, data);
    }

    static boolean isKindOf(UUID uuidType)
    {
        return VD_TYPE_UUID.compareTo(uuidType) == 0;
    }

    protected void didReadRegister(Object id, int register, boolean success, byte[] data)
    {
        if ((this.delegate == null) || (!success) || (data == null)) {
            return;
        }
        switch (register) {
            case 33:
                if (data.length < 3) break;
                short mv = le2s(data, 1);
                this.delegate.updateADC(mv);
                break;
            case 50:
                if (data.length == 0) break;
                State state = State.UNKNOWN;
                if (data[0] == 16)
                    state = State.ARMED;
                else if (data[0] == 17)
                    state = State.STOPPED;
                else if (data[0] == 18)
                    state = State.STARTED;
                else if (data[0] == 19)
                    state = State.RUNNING;
                this.delegate.updateState(state);
                break;
            case 53:
                if (data.length != 2) break;
                this.delegate.updateAlarmConfig(data[0] != 0, data[1] != 0); break;
            case 65:
                if (data.length != 2) break;
                int count = data[0] & 0xFF;
                int period = data[1] & 0xFF;
                period *= 100;
                this.delegate.updateSpeedConfig(period, count);
                break;
            case 66:
                if (data.length == 0) break;
                this.delegate.updateShockSensitivity(le2uc(data, 0)); break;
            case 67:
                if (data.length == 0) break;
                this.delegate.updateShockLevelTable(data); break;
            case 68:
                if (data.length != 4) break;
                this.delegate.updateAlarmMusicConfig(le2uc(data, 0), le2uc(data, 1), le2uc(data, 2), le2uc(data, 3)); break;
            case 69:
                if (data.length <= 1) break;
                byte[] d = new byte[data.length - 1];
                arrayCopyToArray(d, 0, data, 1, data.length - 1);
                this.delegate.updateAlarmNote(le2uc(data, 0), d);
                break;
            case 70:
                if (data.length <= 1) break;
                byte[] d1 = new byte[data.length - 1];
                arrayCopyToArray(d1, 0, data, 1, data.length - 1);
                this.delegate.updateAlarmMusic(le2uc(data, 0), d1);
                break;
        }
    }

    protected void irq(byte[] data)
    {
        if ((data.length > 0) && (this.delegate != null)) {
            int register = (short)data[0] & 0xFF;
            if (register == 53) {
                this.delegate.alarm(data[1]);
            }
            else if (register == 50) {
                State state = State.UNKNOWN;
                if (data[1] == 16)
                    state = State.ARMED;
                else if (data[1] == 17)
                    state = State.STOPPED;
                else if (data[1] == 18)
                    state = State.STARTED;
                else if (data[1] == 19)
                    state = State.RUNNING;
                this.delegate.updateState(state);
            }
        }
    }

    static class Delegate
    {
        protected void alarm(byte type)
        {
        }

        protected void updateADC(short mv)
        {
        }

        protected void updateState(BluxVDSmartGuard.State state)
        {
        }

        protected void updateAlarmConfig(boolean alarmDevice, boolean notifyPhone)
        {
        }

        protected void updateSpeedConfig(int monitorPeriod, int hallSensorCounter)
        {
        }

        protected void updateShockSensitivity(int level)
        {
        }

        protected void updateShockLevelTable(byte[] table)
        {
        }

        protected void updateAlarmMusicConfig(int musicCount, int musicSize, int noteCount, int noteSize)
        {
        }

        protected void updateAlarmNote(int noteID, byte[] data)
        {
        }

        protected void updateAlarmMusic(int musicID, byte[] data)
        {
        }
    }

    static enum Command
    {
        ARM,
        SILENT_ARM,
        DISARM;
    }

    static enum State
    {
        UNKNOWN,
        ARMED,
        STOPPED,
        STARTED,
        RUNNING,
        SILENCE;
    }
    void setHomeState(int nState) {
        byte[] data = new byte[4];
        l2le(nState, data, 0);
        writeRegister(REG_SMART_GUARD_SILENCE_MODE, data);
    }
}