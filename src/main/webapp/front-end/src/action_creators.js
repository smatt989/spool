import axios from 'axios';

const fullstack = false;
const domain = fullstack ? "" : "http://localhost:8080";

export function setState(state) {
    return {
        type: 'SET_STATE',
        state: state
    };
}

export function selectCity(city) {
    return {
        type: 'SELECT_CITY',
        city: city
    };
}

export function fetchCities(){
    const request = axios({
        method: 'get',
        url: `${domain}/cities`,
        header: []
    });

    return {
        type: 'FETCH_CITIES',
        payload: request
    }
}

export function fetchCitiesSuccess(cities){
    return {
        type: 'FETCH_CITIES_SUCCESS',
        payload: cities
    }
}

export function fetchCitiesError(error){
    return {
        type: 'FETCH_CITIES_SUCCESS',
        payload: error
    }
}