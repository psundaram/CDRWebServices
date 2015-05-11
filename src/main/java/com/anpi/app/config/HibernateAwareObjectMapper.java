package com.anpi.app.config;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Represents a HibernateAwareObjectMapper.
 * 
 * <br>Date :  5/24/14.
 *
 * @author rparashar
 * 
 * 
 */
//public class HibernateAwareObjectMapper extends WebMvcConfigurerAdapter {
//
//    public MappingJackson2HttpMessageConverter jacksonMessageConverter(){
//        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//
//        ObjectMapper mapper = new ObjectMapper();
//        //Registering Hibernate4Module to support lazy objects
//        mapper.registerModule(new Hibernate4Module());
//
//        messageConverter.setObjectMapper(mapper);
//        return messageConverter;
//
//    }
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        //Here we add our custom-configured HttpMessageConverter
//        converters.add(jacksonMessageConverter());
//        super.configureMessageConverters(converters);
//    }
//
//}

public class HibernateAwareObjectMapper extends ObjectMapper {

    /**
     * Instantiates a new hibernate aware object mapper.
     */
	
	public HibernateAwareObjectMapper() {
	    super();
	    this.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
	    this.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
	    this.setSerializationInclusion(Include.NON_NULL);
	   }
	
    /*public HibernateAwareObjectMapper() {
        Hibernate4Module hibernate4Module = new Hibernate4Module();
        hibernate4Module.configure(Hibernate4Module.Feature.FORCE_LAZY_LOADING, false);
        hibernate4Module.configure(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION , true);

        registerModule(hibernate4Module);
    }*/
}