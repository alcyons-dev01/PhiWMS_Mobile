package fr.alcyons.phiwms_mobile.Interfaces

interface ScanDebounce
{
    public var mLastScanTime: Long
    public var mScanDebounceMS: Long

    public fun shouldDebounceScan(): Boolean
    {
        val currentTime = System.currentTimeMillis()
        if (currentTime - this.mLastScanTime < this.mScanDebounceMS) { return true }
        this.mLastScanTime = currentTime
        return false
    }

    public fun setScanDebounce(scanDebounceMS: Long) { this.mScanDebounceMS = scanDebounceMS }
}