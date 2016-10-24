package domainapp.dom.division;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.joda.time.LocalDate;

import domainapp.dom.equipo.Equipo;
import domainapp.dom.estado.Estado;
import domainapp.dom.estado.EstadoPartido;
import domainapp.dom.fecha.Fecha;
import domainapp.dom.modalidad.Modalidad;
import domainapp.dom.partido.Partido;
import domainapp.dom.torneo.Torneo;

@DomainService(
        nature = NatureOfService.VIEW,
        repositoryFor = Division.class
)
@DomainServiceLayout(
		named="Planificacion", menuBar=DomainServiceLayout.MenuBar.PRIMARY, menuOrder="6"
)
public class DivisionServicio {
	
	public TranslatableString title() {
        return TranslatableString.tr("Divisiones");
    }
	
	@Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
    		cssClassFa="fa fa-list",
            bookmarking = BookmarkPolicy.AS_ROOT,
            named="Listar Todas las Divisiones"
    )
	@MemberOrder(name="Planificacion", sequence = "6.5")
    public List<Division> listarTodasLasDivisiones() {
        return repositoryService.allInstances(Division.class);
    }
	
	public static class CreateDomainEvent extends ActionDomainEvent<DivisionServicio> {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("deprecation")
		public CreateDomainEvent(final DivisionServicio source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
    }
	
    @Action(
            domainEvent = CreateDomainEvent.class
    )
    @ActionLayout(
    		cssClassFa="fa fa-plus-square"
    )
    @MemberOrder(name="Planificacion", sequence = "6.6")
    public Division crearDivision(
		final @ParameterLayout(named="Nombre") String nombre,
		final @ParameterLayout(named="Estado") Estado estado,
		final @ParameterLayout(named="Torneo") Torneo torneo,
		final @ParameterLayout(named="Modalidad") Modalidad modalidad,
		final @ParameterLayout(named="Puntos GANAR") int puntosGanar,
		final @ParameterLayout(named="Puntos EMPATAR") int puntosEmpatar,
		final @ParameterLayout(named="Puntos PERDER") int puntosPerder
		){
    final Division obj = repositoryService.instantiate(Division.class);
        obj.setNombre(nombre);
        obj.setEstado(estado);
        obj.setTorneo(torneo);
        obj.setModalidad(modalidad);
        obj.setPuntosGanar(puntosGanar);
        obj.setPuntosEmpatar(puntosEmpatar);
        obj.setPuntosPerder(puntosPerder);
        repositoryService.persist(obj);
        return obj;
    }
	
    //POR DEFECTO, AL CREAR LA DIVISION ES ACTIVA
    public Estado default1CrearDivision(){    	
    	return Estado.ACTIVO;
    }    
    //POR DEFECTO, SE SETEA EL VALOR DE PUNTOS
    public int default4CrearDivision(){    	
    	return 3;
    }
    //POR DEFECTO, SE SETEA EL VALOR DE PUNTOS
    public int default5CrearDivision(){    	
    	return 1;
    }
    //POR DEFECTO, SE SETEA EL VALOR DE PUNTOS
    public int default6CrearDivision(){    	
    	return 0;
    }
	
    //METODO PARA CREAR UN FIXTURE
    @ActionLayout(
//    		hidden = Where.NOWHERE,
    		named="Crear Fixture"
    		)
    public Division crearFixture(@ParameterLayout(named="Ingrese Division") final Division division){
    	
    	if (!division.getListaFechas().isEmpty()){
    		JOptionPane.showMessageDialog(null, "No se puede crear un fixture. Ya existe uno");
    		return division;
    	}
    	
    	int tope=division.getListaEquipos().size();
    	int comodin=tope;
    	
    	if (tope<3){
    		JOptionPane.showMessageDialog(null, "Cantidad de equipos insuficiente para crear un fixture (minimo = 3)");
    		return division;
    	}
    	
    	//SI EL NUMERO DE EQUIPOS ES IMPAR, EL TOPE SE INCREMENTA EN 1 UNIDAD
    	if (tope%2!=0){
    		tope=tope+1;
    		comodin=tope;
    	}
    	
		int filas=tope-1;
		int columnas=tope;
		int aux=1;
		int coef1=tope/2-1;
		int coef2=tope-2;
		
		int[][] matrizFixture =new int[filas*2][columnas];
		int[][] matrizAuxiliar =new int[filas*2][columnas];
		
		//HAGO LA PRIMERA LINEA IGUAL A 1, 2, 3, ...tope
		for (int j=0; j<columnas; j++){
			matrizFixture[0][j]=j+1;
		}
		
		//HAGO LA PRIMERA COLUMNA IGUAL A 1, 1, 1, ...1
		for (int i=0; i<filas; i++){
			matrizFixture[i][0]=1;
		}
		
		//HAGO LA SEGUNDA COLUMNA IGUAL TOPE, TOPE-1, TOPE-2, ....  
		for (int i=1; i<filas; i++){
			matrizFixture[i][1]=tope-i+1;
		}
		
		//LOGICA DIFICIL DE EXPLICAR...ARMO EL RESTO DE LA MATRIZ
		for (int i=1; i<filas; i++){
			for (int j=2; j<columnas; j++){
				matrizFixture[i][j]=matrizFixture[i-1][j-1];
			}
		}
		
		//DUPLICO LA MATRIZ PRINCIPAL, CREANDO UNA MATRIZ AUXILIAR
		for (int i=0; i<filas; i++){
			for (int j=0; j<columnas; j++){
				matrizAuxiliar[i][j]=matrizFixture[i][j];
			}
		}
		
		//PERMUTO COLUMNAS
		for (int i=0; i<filas; i++){
			for (int j=1; j<columnas; j++){
				if (j%2!=0){
					matrizFixture[i][j]=matrizAuxiliar[i][columnas-(j+1)/2];
				}
				if (j%2==0){
					matrizFixture[i][j]=matrizAuxiliar[i][j/2];
				}
			}
		}
		
		//DUPLICO OTRA VEZ LA MATRIZ PRINCIPAL
		for (int i=0; i<filas; i++){
			for (int j=0; j<columnas; j++){
				matrizAuxiliar[i][j]=matrizFixture[i][j];
			}
		}
		
		//PERMUTO FILAS
		for (int i=1; i<filas;i++){
			if (i%2!=0){
				for (int j=1; j<columnas;j++){
					matrizFixture[i][j]=matrizAuxiliar[coef1][j];
				}
				coef1=coef1-1;
			}
			if (i%2==0){
				for (int j=1; j<columnas;j++){
					matrizFixture[i][j]=matrizAuxiliar[coef2][j];
				}
				coef2=coef2-1;
			}
		}
		
		//ALTERNO LAS COLUMNAS 1 Y 2 PARA MODIFICAR LA LOCALIA DEL PRIMER EQUIPO
		for (int i=1; i<filas; i++){
			
			if (i%2!=0){
				aux=matrizFixture[i][1];
				matrizFixture[i][1]=1;
				matrizFixture[i][0]=aux;
			}
		}
		
		//CON ESTO, YA OBTENGO UNA MATRIZ FIXTURE QUE SIRVE PARA LA PRIMERA RONDA DE PARTIDOS
		
		//DUPLICO LA LISTA DE EQUIPOS DE LA DIVISION
		List<Equipo>listaEquiposDuplicada=new ArrayList<Equipo>();
		
		//RECORRO LA LISTA DE EQUIPOS DE LA DIVISION Y AGREGO CADA ELEMENTO A LA LISTA DE EQUIPOS DUPLICADA
		Iterator<Equipo> it=division.getListaEquipos().iterator();
		while(it.hasNext()){
			listaEquiposDuplicada.add(it.next());
		}
		
		//DESORDENO LA LISTA DE EQUIPOS DUPLICADA
		java.util.Collections.shuffle(listaEquiposDuplicada);
		
		//AHORA EVALUO SI LA MODALIDAD ES IDA Y VUELTA
		if(division.getModalidad()==Modalidad.IDA_Y_VUELTA){
			
			//DUPLICO LA MATRIZ PRINCIPAL
			for (int i=0; i<filas; i++){
				for (int j=0; j<columnas; j++){
					matrizAuxiliar[i][j]=matrizFixture[i][j];
				}
			}
			
			//EXTIENDO LAS MATRICES FIXTURE Y AUXILIAR
			for (int i=filas; i<filas*2; i++){
				for (int j=0; j<columnas; j++){
					matrizFixture[i][j]=matrizFixture[i-filas][j];
					matrizAuxiliar[i][j]=matrizFixture[i-filas][j];
				}
			}
			
			//PERMUTO LAS COLUMNAS PERO DE LAS FILAS RECIEN AGREGADAS
			for (int i=filas; i<filas*2; i++){
				for (int j=0; j<columnas; j=j+2){
					matrizFixture[i][j]=matrizAuxiliar[i][j+1];
					matrizFixture[i][j+1]=matrizAuxiliar[i][j];
				}
			}
		}
		
//		//METODO PARA MOSTRAR UN JPANEL CON LA MATRIZ FIXTURE
//		String mensaje01="";
//		for (int i=0; i<filas*2;i++){
//			for (int j=0; j<columnas;j++){
//				mensaje01+=Integer.toString(matrizFixture[i][j])+"  ";
//			}
//			mensaje01+="\n";
//		}
//		JOptionPane.showMessageDialog(null, mensaje01);

		//RECORRO LA MATRIZ FIXTURE
		if(division.getModalidad()==Modalidad.IDA_Y_VUELTA)filas=filas*2;
		
		for (int i=0; i<filas; i++){
			
			Fecha fecha=new Fecha();
			fecha.setNroFecha(i+1);
			fecha.setCompleta(false);
			fecha.setDivision(division);
			fecha.getListaPartidos().clear();
			
			for (int j=0; j<columnas; j=j+2){
				
				Partido partido=new Partido();
				
				Equipo eq1=new Equipo();
				Equipo eq2=new Equipo();
				
				if(((matrizFixture[i][j]!=comodin)&&(matrizFixture[i][j+1]!=comodin))||(comodin==division.getListaEquipos().size())){
				
					eq1=listaEquiposDuplicada.get(matrizFixture[i][j]-1);
					eq2=listaEquiposDuplicada.get(matrizFixture[i][j+1]-1);
					
					partido.setEquipoLocal(eq1);
					partido.setEquipoVisitante(eq2);
					partido.setGolesLocal(0);
					partido.setGolesVisitante(0);
					partido.setEstadoPartido(EstadoPartido.PENDIENTE);
					partido.setFecha(fecha);
					partido.setFechaHora(LocalDate.now());
					partido.setNombre("Fe"+Integer.toString(i+1)+"-Pa"+Integer.toString((j+2)/2));
					
					fecha.getListaPartidos().add(partido);
				}
			}
			division.getListaFechas().add(fecha);			
		}
		JOptionPane.showMessageDialog(null, "Fixture creado con exito");
		return division;
    }
    
    @javax.inject.Inject
    RepositoryService repositoryService;
    
    @javax.inject.Inject
    Division division;
}