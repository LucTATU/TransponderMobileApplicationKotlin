package fr.tatu.kartingapp

enum class BLEUUIDAttributes(val uuid: String, val title: String) {
    GENERIC_ACCESS("00001800-0000-1000-8000-00805f9b34fb", "Generic access"),
    GENERIC_ATTRIBUTE("00001801-0000-1000-8000-00805f9b34fb", "Generic attribute"),
    CUSTOM_SERVICE("466c1234-f593-11e8-8eb2-f2801f1b9fd1", "Custom service"),
    DEVICE_NAME("00002a00-0000-1000-8000-00805f9b34fb", "Device name"),
    APPEARANCE("00002a01-0000-1000-8000-00805f9b34fb", "Appearance"),
    CUSTOM_CHARACTERISTIC("466c5678-f593-11e8-8eb2-f2801f1b9fd1", "Custom characteristic"),
    CUSTOM_CHARACTERISTIC_2("466c9abc-f593-11e8-8eb2-f2801f1b9fd1", "Specific characteristic"),
    UNKNOWN_SERVICE("", "Unknown service");

    companion object {
        fun getBLEAttributeFromUUID(uuid: String) = values().firstOrNull { it.uuid == uuid } ?: UNKNOWN_SERVICE
    }
}