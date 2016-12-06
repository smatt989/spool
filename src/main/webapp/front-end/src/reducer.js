import {Map, List} from 'immutable';

function setState(state, newState) {
    return state.merge(newState);
}

function setCity(state, city) {
    return state.set('city', city);
}

function fetchCities(state) {
    return state.set('cityList', {cities: [], error: null, loading: true});
}

function fetchCitiesSuccess(state, cities) {
    return state.set('cityList', {cities: cities, error: null, loading: false});
}

function fetchCitiesError(state, error) {
    return state.set('cityList', {cities: [], error: error, loading: false});
}

export default function(state = Map(), action) {
  switch (action.type) {
  case 'SET_STATE':
    return setState(state, action.state);
  case 'SELECT_CITY':
    return setCity(state, action.city);
  case 'FETCH_CITIES':
    return fetchCities(state);
  case 'FETCH_CITIES_SUCCESS':
    return fetchCitiesSuccess(state, action.payload);
  case 'FETCH_CITIES_ERROR':
    return fetchCitiesError(state, action.payload);
  }
  return state;
}