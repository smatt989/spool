import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import { Grid, Row, Col, Panel, Tabs, Tab } from 'react-bootstrap';
import {connect} from 'react-redux';
import {loadAdventure, loadAdventureSuccess, loadAdventureError, forceMapCenter, setAdventureId} from '../action_creators';
import {MapWrapperContainer} from './MapWrapper';
import {ObjectListContainer} from './ObjectList';
import {TriggerListContainer} from './TriggerList';
import {NavBar} from './NavBar';
import {MapToolbarContainer} from './MapToolbar';
import {MarkerInfoContainer} from './MarkerInfo';
import {TriggerInfoContainer} from './TriggerInfo';

const AppGrid = React.createClass({
    mixins: [PureRenderMixin],
    getAdventureId: function() {
        return parseInt(this.props.params.adventureId) || 0;
    },
    componentDidMount: function() {
        this.props.setAdventureId(this.getAdventureId());
    },
    componentDidUpdate: function(){
        const adventureId = this.props.adventureId;
        if(adventureId != 0){
            this.props.loadAdventure(adventureId);
        }
    },
    render: function() {
        return <Grid>
            <Row>
                <Col>
                    <NavBar />
                    <MarkerInfoContainer />
                    <TriggerInfoContainer />
                </Col>
            </Row>
            <Row className="show-grid">
                <Col lg={8}>
                    <MapToolbarContainer />
                    <MapWrapperContainer />
                </Col>
                <Col lg={4}>
                    <Tabs defaultActiveKey={1} id="uncontrolled-tab-example">
                        <Tab eventKey={1} title="Objects">
                            <ObjectListContainer />
                        </Tab>
                        <Tab eventKey={2} title="Triggers">
                            <TriggerListContainer />
                        </Tab>
                     </Tabs>
                </Col>
            </Row>
        </Grid>
    }

});

const mapDispatchToProps = (dispatch) => {
    return {
        loadAdventure: (adventureId) => {
            dispatch(loadAdventure(adventureId)).then((response) => {
                        if(!response.error){
                            dispatch(loadAdventureSuccess(response.payload.data));
                            dispatch(forceMapCenter());
                        } else {
                            dispatch(loadAdventureError(response.error));
                        }
            })
        },
        setAdventureId: (id) => {
            dispatch(setAdventureId(id));
        }
    }
}

function mapStateToProps(state) {
  return {
    adventureId: state.get('id')
  };
}

export const AppGridContainer = connect(mapStateToProps, mapDispatchToProps)(AppGrid);