package io.github.cottonmc.templates.util;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

//i probalby shouldnt ship this in the mod lol
public class BbModelepic {
	static class Model {
		List<Elem> elements;
	}
	
	static class Elem {
		Map<String, List<Integer>> vertices;
		Map<String, Face> faces;
	}
	
	static class Face {
		Map<String, List<Integer>> uv;
		List<String> vertices;
	}
	
	public static void main(String[] args) {
		Model m = new Gson().fromJson(model, Model.class);
		
		Map<String, List<Integer>> verts = m.elements.get(0).vertices;
		List<Face> faces = m.elements.get(0).faces.values().stream().toList();
		
		List<String> classifications = List.of("TAG_LEFT", "TAG_RIGHT", "TAG_BOTTOM", "TAG_BACK", "TAG_SLOPE");
		Iterator<String> asd = classifications.iterator();
		
		for(Face face : faces) {
			System.out.printf(".tag(%s)%n", asd.next());
			
			for(int i = 0; i < 4; i++) {
				String vertId = face.vertices.get(permute(i));
				List<Integer> coords = verts.get(vertId);
				System.out.printf(".pos(%s, %sf, %sf, %sf)", i, p(coords.get(0)/16f), p(coords.get(1)/16f), p(coords.get(2)/16f));
				
				List<Integer> uv = face.uv.get(vertId);
				System.out.printf(".uv(%s, %sf, %sf)%n", i, p(uv.get(0)/16f), p(uv.get(1)/16f));
			}
			
			System.out.println(".color(-1, -1, -1, -1)\n.emit()");
		}
		System.out.println(';');
	}
	
	//i don't like "16.0f" i'd rather "16f"
	private static String p(float f) {
		if(f == (long) f) return Long.toString((long) f);
		else return Float.toString(f);
	}
	
	private static int permute(int i) {
		if(i == 0) return 2;
		if(i == 1) return 0;
		if(i == 2) return 1;
		if(i == 3) return 3;
		throw new IllegalArgumentException();
	}
	
	private static final String model = """
{"meta":{"format_version":"4.5","model_format":"free","box_uv":false},"name":"","model_identifier":"","visible_box":[1,1,0],"variable_placeholders":"","variable_placeholder_buttons":[],"timeline_setups":[],"unhandled_root_fields":{},"resolution":{"width":16,"height":16},"elements":[{"name":"cuboid","color":4,"origin":[-8,0,-8],"rotation":[0,0,0],"visibility":true,"locked":false,"vertices":{"Iy48":[16,8,16],"dg4s":[16,4,12],"ezd5":[16,0,16],"txzT":[16,0,8],"ZcIz":[0,8,16],"tgTW":[0,0,16],"Q2eV":[0,0,8],"8xsY":[0,4,12]},"faces":{"JSoJzuHO":{"uv":{"Iy48":[0,8],"ezd5":[0,16],"dg4s":[4,12],"txzT":[8,16]},"vertices":["Iy48","ezd5","dg4s","txzT"]},"0I6IF0zk":{"uv":{"ZcIz":[16,8],"tgTW":[16,16],"Q2eV":[8,16],"8xsY":[12,12]},"vertices":["ZcIz","8xsY","tgTW","Q2eV"]},"d7iH7DqS":{"uv":{"ezd5":[16,0],"tgTW":[0,0],"txzT":[16,8],"Q2eV":[0,8]},"vertices":["ezd5","tgTW","txzT","Q2eV"]},"OQ809wKZ":{"uv":{"Iy48":[16,8],"ZcIz":[0,8],"ezd5":[16,16],"tgTW":[0,16]},"vertices":["Iy48","ZcIz","ezd5","tgTW"]},"TSyHMrco":{"uv":{"txzT":[0,16],"Q2eV":[16,16],"Iy48":[0,8],"ZcIz":[16,8]},"vertices":["txzT","Q2eV","Iy48","ZcIz"]}},"type":"mesh","uuid":"3152a30a-ea8e-4ceb-57eb-58319c89815a"}],"outliner":["3152a30a-ea8e-4ceb-57eb-58319c89815a"],"textures":[]}""";
}
