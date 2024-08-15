package com.bolsadeideas.spring.boot.backend.apirest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.spring.boot.backend.apirest.models.services.IClienteService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;


@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")
	//@ResponseStatus(HttpStatus.OK) En las consultas siempre devuelve ok y ok es 200, pero no se agregar porque ya se sobreentiende que viene ok
	public List<Cliente> index(){
		
		return clienteService.findAll();		
	}

	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id){

		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();

		//try catch por si hay error internamente en la conexion o base de datos
		try {
			cliente = clienteService.findById(id);	
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta por id");
			response.put("error", e.getMessage().concat(" : null").concat(e.getMostSpecificCause().getMessage()));		
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		//Por si devuelve un null es decir si no encuentra datos
		if(cliente == null){
			response.put("mensaje","El Cliente ".concat(id.toString()).concat(" no se encuentra registrado en el sistema"));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}

	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result){ //requestBody se usa porque los datos vienen en JSON, entonces esto lo interpreta y lo transforma en el objeto Cliente para el ejemplo

		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();

		if(result.hasErrors()){
			
			List<String> errors = new ArrayList<>();

			for(FieldError err: result.getFieldErrors()){
				errors.add("El campo '"+err.getField()+"' "+err.getDefaultMessage());
			}

			/* expresiones lambda y stream 
			List<String> errors = result.getFieldErrors()
				.stream()
				.map(err -> "El campo '"+err.getField()+"' "+err.getDefaultMessage())
				.collect(Collectors.toList());*/

			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}

		try {
			clienteNew = clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la creación del cliente");
			response.put("error", e.getMessage().concat(" : null").concat(e.getMostSpecificCause().getMessage()));		
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje","El cliente ha sido creado con exito!!");
		response.put("cliente",clienteNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}

	@PutMapping("clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		
		Cliente clienteActual = clienteService.findById(id);
		Cliente clienteUpdate = null;
		Map<String, Object> response = new HashMap<>();

		if(result.hasErrors()){
			List<String> errors = new ArrayList<>();

			for(FieldError err: result.getFieldErrors()){
				errors.add("El campo '"+err.getField()+"' "+err.getDefaultMessage());
			}

			/* expresiones lambda y stream 
			List<String> errors = result.getFieldErrors()
				.stream()
				.map(err -> "El campo '"+err.getField()+"' "+err.getDefaultMessage())
				.collect(Collectors.toList());*/

			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}

		//Por si devuelve un null es decir si no encuentra datos
		if(clienteActual == null){		
			response.put("mensaje","Error: No se puede actualizar, el Cliente ".concat(id.toString()).concat(" no se encuentra registrado en el sistema"));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}

		try {
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());

			clienteUpdate = clienteService.save(clienteActual);						
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la modificación del cliente en la base de datos");
			response.put("error", e.getMessage().concat(" : null").concat(e.getMostSpecificCause().getMessage()));		
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje","El cliente ha sido Modificado con exito!!");
		response.put("cliente",clienteUpdate);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}

	@DeleteMapping("clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){

		Cliente clienteActual = clienteService.findById(id);
		Map<String, Object> response = new HashMap<>();

		//Por si devuelve un null es decir si no encuentra datos
		if(clienteActual == null){		
			response.put("mensaje","Error: No se puede eliminar, el Cliente ".concat(id.toString()).concat(" no se encuentra registrado en el sistema"));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}

		try {
			clienteService.delete(id);			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error no se puede eliminar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(" : null").concat(e.getMostSpecificCause().getMessage()));		
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		response.put("mensaje","Usuario "+id+" eliminado con exito!!");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}	
}