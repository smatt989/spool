import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import { Grid, Row, Col, Panel, Tabs, Tab } from 'react-bootstrap';
import {MapWrapperContainer} from './MapWrapper';
import {ObjectListContainer} from './ObjectList';
import {TriggerListContainer} from './TriggerList';
import {NavBar} from './NavBar';
import {MapToolbarContainer} from './MapToolbar';
import {MarkerInfoContainer} from './MarkerInfo';
import {TriggerInfoContainer} from './TriggerInfo';

export const AppGrid = React.createClass({
    mixins: [PureRenderMixin],
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