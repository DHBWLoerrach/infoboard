package com.example.blackboarddhbwloe.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageFTPClient {


	private static final String TAG = "MyFTPClient";
	public FTPClient mFTPClient = null;
	
	public ImageFTPClient(){
		mFTPClient = new FTPClient();
	}

	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpConnect baut eine Verbindung zum Ftp Server auf. Die String-Variable host 
	 * gibt die Ip-Adresse und die Variablen username und password geben die ben�tigten Logindaten des Ftp-Servers an.
	 * Um die Geschwindigkeit der Up-/Downloads zu erh�hen wird die Bufferger��e auf 1024000 gesetzt.
	 * @return boolean: Erfolg der Methode</b>
	 */
	public boolean ftpConnect() {
		
		final String host = "193.196.4.96";
		final String username = "student";
		final String password = "ponwaru1";
		
		try {
			// connecting to the host
			mFTPClient.connect(host);
			
			// now check the reply code, if positive mean connection success
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
				// login using username & password
				boolean status = mFTPClient.login(username, password);
					
				mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
				mFTPClient.setBufferSize(1024000);
				mFTPClient.enterLocalPassiveMode();
				
				return status;
			}
		} catch (Exception e) {
			Log.d(TAG, "Error: could not connect to host " + host);
		}

		return false;
	}



	/**
	 * <h1> Beschreibung </h1>
	 * <b> Die Methode ftpDisconnect trennt die bestehende Verbindung zum Ftp-Server.
	 * @return boolean: Erfolg der Methode</b>
	 */

	public boolean ftpDisconnect() {
		try {
			mFTPClient.logout();
			mFTPClient.disconnect();
			return true;
		} catch (Exception e) {
			Log.d(TAG, "Error occurred while disconnecting from ftp server.");
		}

		return false;
	}


	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpGetCurrentWorkingDirectory ruft die aktuelle Directory ab und gibt diese als String zur�ck.
	 * @return String: Name der aktuellen Directory</b>
	 */

	public String ftpGetCurrentWorkingDirectory() {
		try {
			String workingDir = mFTPClient.printWorkingDirectory();
			return workingDir;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not get current working directory.");
		}

		return null;
	}


	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpChangeDirectory wechselt zur in der Variable director_path angegebenen Directory.
	 * @param String directory_path: Zieldirectory
	 * @return boolean: Erfolg der Methode</b>
	 */
	public boolean ftpChangeDirectory(String directory_path) {
		try {
			return mFTPClient.changeWorkingDirectory(directory_path);
		} catch (Exception e) {
			Log.d(TAG, "Error: could not change directory to " + directory_path);
		}

		return false;
	}


	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpPrintFilesList gibt eine Liste aller Dateien in der Directory an.
	 * @param string dir_path: Pfad des gew�nschten Directory</b>
	 */
	public void ftpPrintFilesList(String dir_path) {
		try {
			FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
			int length = ftpFiles.length;

			for (int i = 0; i < length; i++) {
				String name = ftpFiles[i].getName();
				boolean isFile = ftpFiles[i].isFile();

				if (isFile) {
					Log.i(TAG, "File : " + name);
				} else {
					Log.i(TAG, "Directory : " + name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpMakeDirectory legt eine neue Directory in der aktuellen Working Directory an.
	 * @param String new_dir_path: Name der neuen Directory
	 * @return boolean: Erfolg der Methode</b>
	 */
	public boolean ftpMakeDirectory(String new_dir_path) {
		try {
			boolean status = mFTPClient.makeDirectory(new_dir_path);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not create new directory named "
					+ new_dir_path);
		}

		return false;
	}


	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpRemoveDirectory l�scht die gew�nschte Directory.
	 * @param dir_path: Pfad der zu l�schenden Directory
	 * @return boolean: Erfolg der Methode</b>
	 */
	public boolean ftpRemoveDirectory(String dir_path) {
		try {
			boolean status = mFTPClient.removeDirectory(dir_path);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not remove directory named " + dir_path);
		}

		return false;
	}

	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpRemoveDirectory l�scht eine gew�nschte Datei.
	 * @param filePath: Pfad und Name der zu l�schenden Datei
	 * @return boolean: Erfolg der Methode</b>
	 */
	public boolean ftpRemoveFile(String filePath) {
		try {
			boolean status = mFTPClient.deleteFile(filePath);
			return status;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpRenameFile benennt eine gew�nschte Datei um.
	 * @param String from: alter Name der Datei
	 * 		  String to: neuer Name der Datei
	 * @return boolean: Erfolg der Methode</b>
	 */
	public boolean ftpRenameFile(String from, String to) {
		try {
			boolean status = mFTPClient.rename(from, to);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Could not rename file: " + from + " to: " + to);
		}

		return false;
	}

	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpDownload l�dt eine Datei vom Ftp-Server und wandelt diese in ein Drawable um.
	 * @param Context context: context der Application zur Cache-Speicherung
	 * 		  String srcFilePath: Pfad und Name der herunterzuladenden Datei
	 * 		  String desFileName: Name f�r die lokale Datei
	 * @return Drawable: Drawable der heruntergeladenen Bildes</b>
	 */
	public Drawable ftpDownload(Context context, String srcFilePath, String desFileName) {

		File cacheDir = context.getCacheDir();

		File ftpFile = null;

		try {
			ftpFile = new File(cacheDir, desFileName);
			Log.d("DHBW ftp", "ftpFile angelegt");
			FileOutputStream desFileStream = new FileOutputStream(ftpFile);
			Log.d("DHBW ftp", "FileStream angelegt");
			Boolean status = mFTPClient
					.retrieveFile(srcFilePath, desFileStream);
			Log.d("DHBW ftp", "Download abgeschlossen: " + status);
			desFileStream.close();
			
			Drawable imageDraw = null;
			imageDraw = Drawable.createFromPath(ftpFile.getAbsolutePath());

			return imageDraw;

		} catch (Exception e) {
			Log.d(TAG, "download failed: " + e);
		}
		return null;

	}

	/**
	 * <h1> Beschreibung </h1>
	 * <b>  Die Methode ftpUpload speichert ein Bild auf dem Ftp-Server.
	 * @param Bitmap bmp: Das zuuploadende Bitmap
	 * 		  String desFileName: Name der Datei auf dem Ftp-Server
	 * 		  String desDirectory: Directory in welche die Datei gespeichert werden soll
	 * 		  Context context: Context der Application
	 * @return boolean: Erfolg der Methode</b>
	 */
	public boolean ftpUpload(Bitmap bmp, String desFileName,
			String desDirectory, Context context) {
		boolean status = false;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			bmp.compress(CompressFormat.JPEG, 100, os);
			byte[] buffer = null;
			buffer = os.toByteArray();
			ByteArrayInputStream is = new ByteArrayInputStream(buffer);
			
			ftpChangeDirectory(desDirectory);
			
			status = mFTPClient.storeFile(desFileName, is);
			
			os.close();
			is.close();
			

			return status;
		} catch (Exception e) {
			Log.d(TAG, "upload failed: " + e);
		}
		
		return status;
	}
	public boolean getFtpConnectState(){
		return mFTPClient.isConnected();
		
	}
}