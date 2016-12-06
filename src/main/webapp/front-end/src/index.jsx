import React from 'react';
import ReactDOM from 'react-dom';
import {Router, Route, hashHistory} from 'react-router';
import {createStore, applyMiddleware} from 'redux';
import {Provider} from 'react-redux';
import reducer from './reducer';
import {setState} from './action_creators';
import App from './components/App';
import {CityListContainer} from './components/CityList';
import {List, Map} from 'immutable';
import promise from 'redux-promise';

const createStoreWithMiddleware = applyMiddleware(
  promise
)(createStore);
const store = createStoreWithMiddleware(reducer);

store.dispatch(setState({
      city: null,
      activityList: Map({activities:[], error: null, loading: false}),
      cityList: Map({cities:[], error: null, loading: false})
  })
);

const routes = <Route component={App}>
  <Route path="/" component={CityListContainer} />
</Route>;

ReactDOM.render(
  <Provider store={store}>
    <Router history={hashHistory}>{routes}</Router>
  </Provider>,
  document.getElementById('app')
);