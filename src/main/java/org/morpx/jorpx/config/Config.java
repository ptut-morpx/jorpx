package org.morpx.jorpx.config;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.morpx.jorpx.Util;

public class Config {
	
	private static class Section {
		public String name;
		public HashMap<String, String> value;
		
		public Section(String name) {
			this.name=name;
			this.value=new HashMap<String, String>();
		}
	}
	
	private ArrayList<Section> conf;
	
	public Config() {
		this.conf=new ArrayList<Section>();
	}
	
	private Section getSection(String section) {
		return Util.CollectionUtil.find(this.conf, (s)->s.name.equals(section));
	}
	private Section requireSection(String section) {
		Section sec=this.getSection(section);
		if(sec==null) {
			this.addSection(section);
			return this.getSection(section);
		}
		return sec;
	}
	
	public List<String> names() {
		ArrayList<String> names=new ArrayList<String>();
		this.conf.forEach((s)->{
			names.add(s.name);
		});
		return names;
	}
	public Set<String> keys(String section) {
		return this.getSection(section).value.keySet();
	}
	public String get(String section, String key) {
		Section sec=this.getSection(section);
		if(sec==null) return null;
		return sec.value.get(key);
	}
	public String getOrDefault(String section, String key, String def) {
		Section sec=this.getSection(section);
		if(sec==null) return def;
		return sec.value.getOrDefault(key, def);
	}
	public void set(String section, String key, String value) {
		Section sec=this.requireSection(section);
		sec.value.put(key, value);
	}
	public void setIfDefault(String section, String key, String value) {
		Section sec=this.requireSection(section);
		if(!sec.value.containsKey(key)) sec.value.put(key, value);
	}
	public void clear(String section, String key) {
		Section sec=this.requireSection(section);
		sec.value.remove(key);
	}
	public void clear(String section) {
		Section sec=this.requireSection(section);
		sec.value.clear();
	}
	
	public void addSection(String section) {
		if(getSection(section)!=null) return;
		this.conf.add(new Section(section));
	}
	public Section removeSection(String section) {
		Section sec=this.getSection(section);
		this.conf.remove(sec);
		return sec;
	}
	
	public void read(File configFile) throws IOException {
		this.read(configFile.toPath());
	}
	public void read(Path configFile) throws IOException {
		if(!Files.isReadable(configFile)) Files.createFile(configFile);
		List<String> lines=Files.readAllLines(configFile);
		Section current=null;
		for(String line:lines) {
			if(line.startsWith(";")||line.startsWith("#")) continue;
			line=line.trim();
			if(line.isEmpty()) continue;
			if(line.startsWith("[")) {
				String secName=line.substring(1, line.length()-1).toLowerCase();
				current=requireSection(secName);
			} else {
				String[] sp=line.split("=", 2);
				String k=sp[0].trim().toLowerCase();
				String v=sp[1].trim();
				current.value.put(k, v);
			}
		}
	}
	
	public static Config readConfig(File configFile) throws IOException {
		Config cfg=new Config();
		cfg.read(configFile);
		return cfg;
	}
	public static Config readConfig(Path configFile) throws IOException {
		Config cfg=new Config();
		cfg.read(configFile);
		return cfg;
	}
	
	public void dump(PrintStream out, boolean javaSyntax) {
		List<String> names=names();
		names.forEach((name)->{
			out.println('['+name+']');
			Set<String> keys=keys(name);
			keys.forEach((key)->{
				if(javaSyntax) {
					out.print('"'+key+"\" = ");
					out.println('"'+get(name, key)+'"');
				}else {
					out.print(key+" = ");
					out.println(get(name, key));
				}
			});
			out.println();
		});
	}
	
	public void writeConfig(File configFile) throws IOException {
		dump(System.err, false);
		PrintStream pst=new PrintStream(configFile);
		dump(pst, false);
		pst.close();
	}
	public void writeConfig(Path configFile) throws IOException {
		writeConfig(configFile.toFile());
	}
}
