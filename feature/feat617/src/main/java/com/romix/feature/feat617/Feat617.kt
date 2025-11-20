package com.romix.feature.feat617

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat617Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat617UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat617FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat617UserSummary
)

data class Feat617UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat617NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat617Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat617Config = Feat617Config()
) {

    fun loadSnapshot(userId: Long): Feat617NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat617NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat617UserSummary {
        return Feat617UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat617FeedItem> {
        val result = java.util.ArrayList<Feat617FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat617FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat617UiMapper {

    fun mapToUi(model: List<Feat617FeedItem>): Feat617UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat617UiModel(
            header = UiText("Feat617 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat617UiModel =
        Feat617UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat617UiModel =
        Feat617UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat617UiModel =
        Feat617UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat617Service(
    private val repository: Feat617Repository,
    private val uiMapper: Feat617UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat617UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat617UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat617UserItem1(val user: CoreUser, val label: String)
data class Feat617UserItem2(val user: CoreUser, val label: String)
data class Feat617UserItem3(val user: CoreUser, val label: String)
data class Feat617UserItem4(val user: CoreUser, val label: String)
data class Feat617UserItem5(val user: CoreUser, val label: String)
data class Feat617UserItem6(val user: CoreUser, val label: String)
data class Feat617UserItem7(val user: CoreUser, val label: String)
data class Feat617UserItem8(val user: CoreUser, val label: String)
data class Feat617UserItem9(val user: CoreUser, val label: String)
data class Feat617UserItem10(val user: CoreUser, val label: String)

data class Feat617StateBlock1(val state: Feat617UiModel, val checksum: Int)
data class Feat617StateBlock2(val state: Feat617UiModel, val checksum: Int)
data class Feat617StateBlock3(val state: Feat617UiModel, val checksum: Int)
data class Feat617StateBlock4(val state: Feat617UiModel, val checksum: Int)
data class Feat617StateBlock5(val state: Feat617UiModel, val checksum: Int)
data class Feat617StateBlock6(val state: Feat617UiModel, val checksum: Int)
data class Feat617StateBlock7(val state: Feat617UiModel, val checksum: Int)
data class Feat617StateBlock8(val state: Feat617UiModel, val checksum: Int)
data class Feat617StateBlock9(val state: Feat617UiModel, val checksum: Int)
data class Feat617StateBlock10(val state: Feat617UiModel, val checksum: Int)

fun buildFeat617UserItem(user: CoreUser, index: Int): Feat617UserItem1 {
    return Feat617UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat617StateBlock(model: Feat617UiModel): Feat617StateBlock1 {
    return Feat617StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat617UserSummary> {
    val list = java.util.ArrayList<Feat617UserSummary>(users.size)
    for (user in users) {
        list += Feat617UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat617UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat617UiModel {
    val summaries = (0 until count).map {
        Feat617UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat617UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat617UiModel> {
    val models = java.util.ArrayList<Feat617UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat617AnalyticsEvent1(val name: String, val value: String)
data class Feat617AnalyticsEvent2(val name: String, val value: String)
data class Feat617AnalyticsEvent3(val name: String, val value: String)
data class Feat617AnalyticsEvent4(val name: String, val value: String)
data class Feat617AnalyticsEvent5(val name: String, val value: String)
data class Feat617AnalyticsEvent6(val name: String, val value: String)
data class Feat617AnalyticsEvent7(val name: String, val value: String)
data class Feat617AnalyticsEvent8(val name: String, val value: String)
data class Feat617AnalyticsEvent9(val name: String, val value: String)
data class Feat617AnalyticsEvent10(val name: String, val value: String)

fun logFeat617Event1(event: Feat617AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat617Event2(event: Feat617AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat617Event3(event: Feat617AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat617Event4(event: Feat617AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat617Event5(event: Feat617AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat617Event6(event: Feat617AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat617Event7(event: Feat617AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat617Event8(event: Feat617AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat617Event9(event: Feat617AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat617Event10(event: Feat617AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat617Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat617Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat617Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat617Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat617Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat617Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat617Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat617Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat617Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat617Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat617(u: CoreUser): Feat617Projection1 =
    Feat617Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat617Projection1> {
    val list = java.util.ArrayList<Feat617Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat617(u)
    }
    return list
}
