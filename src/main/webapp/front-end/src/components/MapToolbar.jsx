import React from 'react';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {Button, ButtonToolbar, FormGroup, FormControl, Navbar, ListGroup, ListGroupItem} from 'react-bootstrap';
import {forceMapCenter, mapJumpTo, clearRemoteLocationSearchResults, searchRemoteLocations, searchRemoteLocationsSuccess, searchRemoteLocationsError} from '../action_creators'

const MapToolbar = React.createClass({
    mixins: [PureRenderMixin],
    getSearchResults: function() {
        return this.props.remoteLocationsList.locations || []
    },
    searchWithQuery: function(event){
        if(event.target.value.length > 2){
            this.props.searchRemoteLocations(event.target.value);
        }
    },
    selectSearchResult: function(latlng, locationType){
        this.props.mapJumpTo(latlng, locationType);
        this.props.clearRemoteLocationSearchResults();
    },
    render: function(){
        const selectSearchResult = this.selectSearchResult
        return   <ButtonToolbar>
                   <Button onClick={this.props.centerMap}>Center Map</Button>
                   <Navbar.Form pullLeft>
                    <FormGroup>
                      <FormControl id="search-input" ref={(input) => {this.searchInput = input}} onKeyUp={this.searchWithQuery} type="text" placeholder="Search" />
                      <ListGroup className="map-search-results">
                        {this.getSearchResults().map(result =>
                            <ListGroupItem onClick={() => selectSearchResult({lat: result.geometry.coordinates[1], lng: result.geometry.coordinates[0]}, result.properties.layer)} key={result.properties.id} href="#">
                                <h6>{result.properties.label}</h6>
                                <i className="small-details">{result.properties.layer}</i>
                            </ListGroupItem>
                        )}
                      </ListGroup>
                    </FormGroup>
                    </Navbar.Form>
                 </ButtonToolbar>;
    }
});

function mapStateToProps(state) {
  return {
    remoteLocationsList: state.get('remoteLocationsList')
  };
}

const mapDispatchToProps = (dispatch) => {
    return {
        centerMap: () => {
            dispatch(forceMapCenter())
        },
        searchRemoteLocations: (query) => {
            dispatch(searchRemoteLocations(query)).then((response) => {
               !response.error ? dispatch(searchRemoteLocationsSuccess(response.payload.data.features)) : dispatch(searchRemoteLocationsError(response.payload));
           });
        },
        mapJumpTo: (latlng, locationType) => {
            dispatch(mapJumpTo(latlng, locationType))
        },
        clearRemoteLocationSearchResults: () => {
            dispatch(clearRemoteLocationSearchResults())
        }

    }
}

export const MapToolbarContainer = connect(mapStateToProps, mapDispatchToProps)(MapToolbar);

function showProps(obj){
    for(var key in obj){
        console.log(key+": "+obj[key]);
    }
}