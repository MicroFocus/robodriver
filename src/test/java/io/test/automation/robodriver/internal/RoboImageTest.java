package io.test.automation.robodriver.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import io.test.automation.robodriver.RoboDriverFindImageTest;

public class RoboImageTest {
	
	static File testImageToFind_png = new File(RoboDriverFindImageTest.class.getClassLoader().getResource("test_image_1.png").getFile());
	static File testImageToFind_jpg = new File(RoboDriverFindImageTest.class.getClassLoader().getResource("test_image_1.jpg").getFile());

	@Test
	public void testCreateImageFromFileUriPng() throws IOException {
		RoboImage image = new RoboImage(testImageToFind_png.toURI().toString());
		assertNotNull(image);
		assertTrue(new RoboUtil().matchImages(ImageIO.read(testImageToFind_png), image.getBufferedImage()));
	}
	
	@Test
	public void testCreateImageFromFileUriJpg() throws IOException {
		RoboImage image = new RoboImage(testImageToFind_jpg.toURI().toString());
		assertNotNull(image);
		assertTrue(new RoboUtil().matchImages(ImageIO.read(testImageToFind_jpg), image.getBufferedImage()));
	}
	
	@Test
	public void testCreateImageFromDataUriPng() throws IOException {
		RoboImage image = new RoboImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAClSURBVDhPpdFbDoUgDEXR4zz0T+c/Mp2CeoglRUupsBMvmktXfEwAzrt7ASZedfSM41i2rHEZap/XtGZpBBWMFU4PWmKG8QfVGDNBFkHfGKuCzEMtjLkgs/6sYawJMr3Bw1gIZNzUwlgYJBbZGAL1nbU2N0HrMb0BF/TeWW2oCsY+wHfQBCOY9B7+gH8wSQMJfM67MKlA+TOCSYJCY+mWOw4JwHkBGJ9CpvqlAE0AAAAASUVORK5CYII=");
		assertNotNull(image);
		assertTrue(new RoboUtil().matchImages(ImageIO.read(testImageToFind_png), image.getBufferedImage()));
	}
	
	@Test
	public void testCreateImageFromDataUriJpg() throws IOException {
		RoboImage image = new RoboImage("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/4QBaRXhpZgAATU0AKgAAAAgABQMBAAUAAAABAAAASgMDAAEAAAABAAAAAFEQAAEAAAABAQAAAFERAAQAAAABAAAOxFESAAQAAAABAAAOxAAAAAAAAYagAACxj//bAEMAAgEBAgEBAgICAgICAgIDBQMDAwMDBgQEAwUHBgcHBwYHBwgJCwkICAoIBwcKDQoKCwwMDAwHCQ4PDQwOCwwMDP/bAEMBAgICAwMDBgMDBgwIBwgMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDP/AABEIABQAFAMBIgACEQEDEQH/xAAfAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEBAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/APvD4NfBr4az/s1/CX4sfFj4S/Cvxd8L/F3gfR9TuLjUvC9rqH/Cn/tNql01vA1yssieGFlnlKoXb+yd5OTpxJ0v3L4lfsIfs1+CvsWnad+zb8Dta8U63vXSdJXwVpcPn7NvmTzSfZm8i1i3oZZirbd6IiyzSwwyn7CHxK/4Qr/gmx+zbp2nWX9teKdb+Gnh9dJ0lZvJ8/Zpdp5k80m1vItYt6GWYq23eiIss0sMMtH4a/DX/h2V9t1HUb3+2/hTrextW1ZofJ/4Vjs3eXBDHubyPCsW9xFCGb+yN7u7S2cs02nfXZ9n2aYLM8TgMvxNSnTp1JxSjOSjGKk7JJO22y6bvon14LBUIUI4zGRTUl7ses31ba1UE9G1ZyacINNSlD85v+C2X7e2qf8ABL39pTwZ8OdJ+FPwB+I0Fx4HsdVa98W+DXuf7N/0q8tUsNPgjuUjstOhitY1htkB25dneWWSSVyvC/8Ag71/5ST+CP8Asmlh/wCnTVqK/wBHPBzwR4EzfgvLszzTK6VWvVpqU5yTcpSbd3J31b6vqfLZhxpn1LEzp0sXUjFPSMZOMUuyirJJdEkklokke6ft7f8ABbL4lf8ABL3xT8KdJ+HPgz4V3EHxG+Feg+Lb1tV0i6/4lvmvdxwaZZJbXUEdvp1pFGqQW6p8u6V2aSWWWR/C/wDiL1/aU/6Ej4Hf+CbVP/ljRRX8ycAcPZViuHcJicVhqc6k4JylKEXKTerbbTbberb1Z7XFlacc6xVOLaUakopX0UYu0Ul0SSSSWiSSWhkf8FRv+Co11+xHqfwGsbH4Dfs1/ETT/iJ8HPD/AI8gg8eeCTrf/CHfbzcmTRtJkM6SQaRDLHJLb2sry/Z/tMkUTpbpBBCUUV/PGM4hzXC4iph8NiakIRlJKMZyUUrvRJNJLyRMaMJJNpX9D//Z");
		assertNotNull(image);
		assertTrue(new RoboUtil().matchImages(ImageIO.read(testImageToFind_jpg), image.getBufferedImage()));
	}
}
