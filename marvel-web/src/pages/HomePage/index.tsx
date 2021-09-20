import React from 'react';
import { Card, Carousel } from 'antd';

const contentStyle = {
  height: '200px',
  color: '#fff',
  lineHeight: '160px',
  textAlign: 'center',
  background: '#364d79',
};

export default (): React.ReactNode => {
  // return <Title level={3}>首页</Title>;
  return (
    <>
      <Card style={{ width: '100%' }} bodyStyle={{ padding: 8 }}>
        <Carousel autoplay>
          <div>
            <h3 style={contentStyle}>1</h3>
          </div>
          <div>
            <h3 style={contentStyle}>2</h3>
          </div>
          <div>
            <h3 style={contentStyle}>3</h3>
          </div>
          <div>
            <h3 style={contentStyle}>4</h3>
          </div>
        </Carousel>
      </Card>
    </>
  )
};
