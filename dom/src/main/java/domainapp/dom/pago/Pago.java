/*
#	This file is part of SIFHON.
#
#	Copyright ( C ) 2016 , SIFHON
#
#   SIFHON is free software: you can redistribute it and/or modify
#   it under the terms of the GNU General Public License as published by
#   the Free Software Foundation, either version 3 of the License, or
#   (at your option) any later version.
#
#   SIFHON is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#   GNU General Public License for more details.
#
#   You should have received a copy of the GNU General Public License
#   along with SIFHON.  If not, see <http://www.gnu.org/licenses/>.
*/

package domainapp.dom.pago;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.joda.time.LocalDate;

import domainapp.dom.jugador.Jugador.NameDomainEvent;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Pago {
	
	//NRORECIBO
	@MemberOrder(sequence = "1")
	@Column(allowsNull="false")
	@Property(domainEvent = NameDomainEvent.class)
	@PropertyLayout(named="Recibo")
	private String nroRecibo;
	public String getNroRecibo() {return nroRecibo;}
	public void setNroRecibo(String nroRecibo) {this.nroRecibo = nroRecibo;}

	//FECHA DE PAGO
	@MemberOrder(sequence = "2")
	@Column(allowsNull="false")
	@Property(domainEvent = NameDomainEvent.class)
	@PropertyLayout(named="Fecha de Pago")
	private LocalDate fechaDePago;
	public LocalDate getFechaDePago() {return fechaDePago;}
	public void setFechaDePago(LocalDate fechaDePago) {this.fechaDePago = fechaDePago;}

	//VALOR
	@MemberOrder(sequence = "3")
    @Column(allowsNull="false")
    @Property(domainEvent = NameDomainEvent.class, editing=Editing.DISABLED)
	@PropertyLayout(named="Monto")
    private BigDecimal valor;
	public BigDecimal getValor() {return valor;}
	public void setValor(BigDecimal valor) {this.valor = valor;}
}