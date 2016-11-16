package domainapp.dom.cuotajugador;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.util.ObjectContracts;

import com.google.common.collect.Lists;

import domainapp.dom.cuota.Cuota;
import domainapp.dom.jugador.Jugador;
import domainapp.dom.jugador.JugadorServicio;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "simple",
        table = "CuotaJugador")
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="cuotaJugador_id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "traerTodos", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.cuotajugador.CuotaJugador")
})
@javax.jdo.annotations.Unique(name="CuotaJugador_UNQ", members = {"temporada","nombre"})
@DomainObject(bounded=true)
@DomainObjectLayout
public class CuotaJugador extends Cuota implements Comparable<CuotaJugador> {

	public TranslatableString title() {
		return TranslatableString.tr("{nombre}", "nombre",
				"Cuota de Jugador: " + this.getNombre());
	}
	
	public String iconName(){return "CuotaJugador";}
	
	//LISTA DE JUGADORES
  	@MemberOrder(sequence = "5")
  	@Persistent(mappedBy = "cuotasJugador")
  	@CollectionLayout(named="Lista de Jugadores que deben Pagar")
  	private SortedSet<Jugador> listaJugadores=new TreeSet<Jugador>();
  	public SortedSet<Jugador> getListaJugadores() {return listaJugadores;}
	public void setListaJugadores(final SortedSet<Jugador> listaJugadores) {this.listaJugadores = listaJugadores;}

	//METODO PARA AGREGAR CUOTA	A UN JUGADOR	
	@MemberOrder(sequence = "6")
	@ActionLayout(named="Agregar Cuota a un Jugador", cssClassFa="fa fa-plus")
	public CuotaJugador agregarJugadorACuota(final Jugador e) {
		if(e == null || listaJugadores.contains(e)) return this;
		listaJugadores.add(e);
		e.getCuotasJugador().add(this);
		return this;
	}
	
	//METODO PARA QUITAR CUOTA A UN JUGADOR	
	@MemberOrder(sequence = "7")
	@ActionLayout(named="Quitar Cuota a un Club", cssClassFa="fa fa-minus")
	public CuotaJugador quitarJugadorACuota(final Jugador e) {
		if(e == null || !listaJugadores.contains(e)) return this;
		listaJugadores.remove(e);
		e.getCuotasJugador().remove(this);
		return this;
	}
	
	public List<Jugador> choices0QuitarJugadorACuota(){
		
			return Lists.newArrayList(getListaJugadores());
	}
	
	//METODO PARA AGREGAR TODAS LAS CUOTAS AL JUGADOR
	@MemberOrder(sequence = "8")
	@ActionLayout(named="Agregar cuota a TODOS los jugadores", cssClassFa="fa fa-thumbs-o-up")
	public CuotaJugador agregarTodas(){
		for (Iterator<?> it=jugadorServicio.listarTodosLosJugadores().iterator();it.hasNext();){
			Jugador jug=((Jugador)it.next());
			jug.getCuotasJugador().add(this);
		}
		return this;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("deprecation")
	@Override
	public int compareTo(final CuotaJugador other) {
		// TODO Auto-generated method stub
		return ObjectContracts.compare(this, other, "temporada", "nombre");
	}
	
	@javax.inject.Inject
    JugadorServicio jugadorServicio;	
}