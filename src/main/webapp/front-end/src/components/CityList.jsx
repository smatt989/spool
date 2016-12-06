import React from 'react';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import City from './City';

import {fetchCities, fetchCitiesSuccess, fetchCitiesError} from '../action_creators';

export const CityList = React.createClass({
  mixins: [PureRenderMixin],
  getError: function () {
    return this.props.cityList.error || null;
  },
  getIsLoading: function() {
    return this.props.cityList.loading || false;
  },
  getCities: function() {
    return this.props.cityList.cities || [];
  },
  componentDidMount: function() {
    this.props.fetchCities();
  },
  render: function() {
        if(this.getIsLoading()) {
          return <div className="container"><h1>Cities</h1><h3>Loading...</h3></div>
        } else if(this.getError()) {
          return <div className="alert alert-danger">Error: {error.message}</div>
        }

      return <div className="city-list">
        {this.getCities().map(city =>
            <City city={city} {...this.props} />
        )}
      </div>;
  }
});

function mapStateToProps(state) {
  return {
    cityList: state.get('cityList')
  };
}

const mapDispatchToProps = (dispatch) => {
    return {
        fetchCities: () => {
            dispatch(fetchCities()).then((response) => {
               !response.error ? dispatch(fetchCitiesSuccess(response.payload.data)) : dispatch(fetchCitiesError(response.payload.data));
           });
        }
    }
}

export const CityListContainer = connect(mapStateToProps, mapDispatchToProps)(CityList);