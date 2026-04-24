package fr.alcyons.phiwms_mobile.Interfaces

import android.database.sqlite.SQLiteDatabase

interface DatabaseProvider {
    val db: SQLiteDatabase // remplace AppDatabase par ton type réel
}