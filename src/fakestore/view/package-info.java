/**
 * This package contains the view classes of the FakeStore.
 * The view is responsible for displaying the current state of the application.
 * <p>
 * This package contains {@link fakestore.view.IView} which is the interface the controller can use to communicate.
 * The implementation {@link fakestore.view.View} does use the interfaces
 * {@link fakestore.controller.IRoute}, {@link fakestore.controller.IClickable} and {@link fakestore.controller.ITypeControl}
 * so that the controller can properly utilize the view.
 *
 * @author Ufuk Ustali
 */
package fakestore.view;