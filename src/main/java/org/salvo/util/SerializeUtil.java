package org.salvo.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 序列化工具
 * 
 * @author 周诚卓
 *
 */
public class SerializeUtil {

	public final static Logger logger = LoggerFactory
			.getLogger(SerializeUtil.class);

	/**
	 * 序列化
	 * 
	 * @param <T>
	 * 
	 * @param object
	 * @return
	 */
	public static <T> byte[] serialize(T object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("序列化失败");
		}
		return null;
	}

	/**
	 * 反序列化
	 * 
	 * @param <T>
	 * 
	 * @param bytes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("反序列化失败");
		}
		return null;
	}

	/**
	 * 创建保存文件
	 * 
	 * @param fileDirectoryAndName
	 *            路径名+文件名
	 * @param fileContent
	 *            文件内容
	 */
	public static void createNewFile(String fileDirectoryAndName,
			String fileContent) {
		try {
			String fileName = fileDirectoryAndName;
			// 创建File对象，参数为String类型，表示目录名
			File myFile = new File(fileName);
			// 判断文件是否存在，如果不存在则调用createNewFile()方法创建新目录，否则跳至异常处理代码
			if (!myFile.exists())
				myFile.createNewFile();
			else
				// 如果不存在则扔出异常
				throw new Exception("The new file already exists!");
			// 下面把数据写入创建的文件，首先新建文件名为参数创建FileWriter对象
			FileWriter resultFile = new FileWriter(myFile);
			// 把该对象包装进PrinterWriter对象
			PrintWriter myNewFile = new PrintWriter(resultFile);
			// 再通过PrinterWriter对象的println()方法把字符串数据写入新建文件
			myNewFile.println(fileContent);
			resultFile.close(); // 关闭文件写入流
		} catch (Exception ex) {
			System.out.println("无法创建新文件！");
			ex.printStackTrace();
		}
	}

	/**
	 * 读取文件内容
	 * 
	 * @param filePath
	 *            文件路径
	 * @return
	 */
	public static String readTxtFile(String filePath) {
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// System.out.println("------->"+lineTxt);
					return lineTxt;
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return null;
	}
}
