import React from 'react';
import ReactDOM from 'react-dom';
import {Router, Route, hashHistory} from 'react-router';
import {createStore, applyMiddleware} from 'redux';
import {Provider} from 'react-redux';
import reducer from './reducer';
import {setState} from './action_creators';
import App from './components/App';
import {List, Map} from 'immutable';
import promise from 'redux-promise';
import {AppGrid} from './components/AppGrid';
//import {MapWrapperContainer} from './components/MapWrapper'

const createStoreWithMiddleware = applyMiddleware(
  promise
)(createStore);
const store = createStoreWithMiddleware(reducer, window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());


store.dispatch(setState(Map({
      latLng: Map({lat: null, lng: null}),
      objectCount: 2,
      triggerCount: 0,
      triggers: List.of(),
      markers: List.of(Map({
                      latlng: Map({
                        lat: 40.734785,
                        lng: -73.990659
                      }),
                      key: 'c3f29710-c1ac-11e6-b20d-55067f040c32',
                      title: 'Waypoint 1'
                    }),
                    Map({
                      latlng: Map({
                        lat: 40.729407,
                        lng: -73.988165
                      }),
                      key: 'c45acec0-c1ac-11e6-b20d-55067f040c32',
                      title: 'Waypoint 2'
                    })),
      remoteLocationsList: Map({locations:List.of(), error: null, loading: false}),
      forceMapCenter: true,
      mapZoom: 13,
      selectedMarker: null,
      editingMarker: null,
      editingTrigger: null
  }))
);

const routes = <Route component={App}>
  <Route path="/" component={AppGrid} />
</Route>;



ReactDOM.render(
  <Provider store={store}>
    <Router history={hashHistory}>{routes}</Router>
  </Provider>,
  document.getElementById('app')
);

