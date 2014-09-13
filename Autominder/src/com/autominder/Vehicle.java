package com.autominder;

import android.annotation.SuppressLint;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Vehicle implements Serializable{

	private static final long serialVersionUID = -7614135801653779184L;
	
	private String name;
	private boolean favorite;
	/**
	 * es un numero que indica el numero de kilometros SEMANALES
	 */
	private int weeklyKM;
	private int currentKmCount;
	private Date lastDayChecked;
	private ArrayList<Maintenance> maintenances;
	private ArrayList<Record> records;
	private ArrayList<Reminder> reminders;

	public Vehicle(String name, int weeklyKM, int currentKmCount,
			ArrayList<Maintenance> maintenances, ArrayList<Record> records) {
		this.name = name;
		this.weeklyKM = weeklyKM;
		this.currentKmCount = currentKmCount;
		lastDayChecked = new Date();
		this.maintenances = maintenances;
		this.records = records;
		reminders = new ArrayList<Reminder>();//los reminders los calcula el vehicle
		calcularRecordatorios();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	public ArrayList<Maintenance> getMaintenances() {
		return maintenances;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isFavorite() {
		return favorite;
	}
	
	public int getWeeklyKM() {
		return weeklyKM;
	}
	
	public ArrayList<Reminder> getReminders() {
		return reminders;
	}
	
	public ArrayList<Record> getRecords() {
		return records;
	}
	
	public void setWeeklyKM(int weeklyKM) {
		this.weeklyKM=weeklyKM;
		calcularRecordatorios();
	}

	/**
	 * Por cada mantenimiento de lista de mantenimientos (segun km) deseados
	 * para el carro, tome su km y con base a los km recorridos desde el ultimo
	 * record, recalcule la siguiente fecha. Cree todos los reminders y asignelos al
	 * atributo 'reminders' 
	 */
	private void calcularRecordatorios() {
		ArrayList<Reminder> brandNewReminders = new ArrayList<Reminder>();
		for(int i = 0; i<maintenances.size(); i++){
			Maintenance m = maintenances.get(i);
			if(m.getType() == Maintenance.SEGUN_KM){
				int kmsUntilNext = m.getKm() - darRecordPorMantenimiento(m.getNombre()).getKmPassedSince();
				int daysUntilNext = 7*kmsUntilNext/weeklyKM;
				/**
				 * si en una semana se recorren weeklyKM km, i.e, 1sem/weeklyKM km,
				 * entonces se debe multiplicar por 7 para obtener 7dias/weeklyKM km, 
				 * y finalmente se multiplica por 'kmsUntilNext' km;
				 * obteniendo 7*kmsUntilNext/weeklyKM dias
				 */
				Reminder rem = new Reminder(m.getNombre(), new Date(new Date().getTime() + daysUntilNext*24*60*60*1000));
				brandNewReminders.add(rem);
			}
		}
		reminders = brandNewReminders;
		//voila, bitch.
	}

	private Record darRecordPorMantenimiento(String nombre) {
		for (int i = 0; i < records.size(); i++) {
			Record r = records.get(i);
			if(r.getMaintenanceName().equals(nombre))return r;
		}
		return null;
	}

	@SuppressLint("SimpleDateFormat") 
	public int getCurrentKmCount() {
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-YYYY");
		try {
			if(new Date().getTime()>df.parse(df.format(lastDayChecked)).getTime()+24*60*60*1000)//si es al menos el siguiente dia a la ultima vez
			{
				//entonces se averigua cuantos dias han pasado,
				int daysPassed = (int) ((new Date().getTime() - df.parse(df.format(lastDayChecked)).getTime()+24*60*60*1000)/(24*60*60*1000));
				
				//se actualizan los KmPassedSince de los records y
				for (int i = 0; i < records.size(); i++) {
					Record r = records.get(i);
					r.setKmPassedSince(r.getKmPassedSince()+(weeklyKM*daysPassed/7));
				}
				
				//se actualiza el currentKmCount y el lastDayChecked. Pretty cool right?
				currentKmCount+=weeklyKM*daysPassed/7;
				lastDayChecked = new Date();
				return currentKmCount;
				
			}else{
				return currentKmCount;
			}
		} catch (ParseException e) {
			// Nunca entra a esta excepcion...
			e.printStackTrace();
			return 999999;
		}
		
	}
	
	/**
	 * Al usuario puede darle la gana de modificar su actual cuenta de km,
	 * esta nueva cuenta de km puede ser menor o mayor a la actual; como sea, 
	 * hay que restar/sumar lo necesario de la actual cuenta de km y, asi mismo,
	 * de los kmPassedSince de los records... sin mencionar que toca recalcular
	 * los reminders.
	 * @param newCurrentKmCount
	 */
	public void modifyCurrentKmCount(int newCurrentKmCount){
		int diferencia = newCurrentKmCount-currentKmCount;
		currentKmCount = newCurrentKmCount;
		for (int i = 0; i < records.size(); i++) {
			Record r = records.get(i);
			r.setKmPassedSince(r.getKmPassedSince()+diferencia);
		}
		calcularRecordatorios();
	}
	
	/**
	 * El ususario tambien puede darse cuenta de que su estimado de kilometraje
	 * semanal fue una burrada, y querra cambiarlo. Ni modo. Tocara entonces
	 * guardar el nuevo kilometraje semanal y recalcular los reminders.
	 * @param newWeeklyKM
	 */
	public void modifyWeeklyKM(int newWeeklyKM){
		weeklyKM = newWeeklyKM;
		calcularRecordatorios();
	}
	
}
