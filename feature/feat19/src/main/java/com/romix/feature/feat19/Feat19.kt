package com.romix.feature.feat19

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat19Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat19UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat19FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat19UserSummary
)

data class Feat19UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat19NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat19Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat19Config = Feat19Config()
) {

    fun loadSnapshot(userId: Long): Feat19NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat19NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat19UserSummary {
        return Feat19UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat19FeedItem> {
        val result = java.util.ArrayList<Feat19FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat19FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat19UiMapper {

    fun mapToUi(model: List<Feat19FeedItem>): Feat19UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat19UiModel(
            header = UiText("Feat19 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat19UiModel =
        Feat19UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat19UiModel =
        Feat19UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat19UiModel =
        Feat19UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat19Service(
    private val repository: Feat19Repository,
    private val uiMapper: Feat19UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat19UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat19UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat19UserItem1(val user: CoreUser, val label: String)
data class Feat19UserItem2(val user: CoreUser, val label: String)
data class Feat19UserItem3(val user: CoreUser, val label: String)
data class Feat19UserItem4(val user: CoreUser, val label: String)
data class Feat19UserItem5(val user: CoreUser, val label: String)
data class Feat19UserItem6(val user: CoreUser, val label: String)
data class Feat19UserItem7(val user: CoreUser, val label: String)
data class Feat19UserItem8(val user: CoreUser, val label: String)
data class Feat19UserItem9(val user: CoreUser, val label: String)
data class Feat19UserItem10(val user: CoreUser, val label: String)

data class Feat19StateBlock1(val state: Feat19UiModel, val checksum: Int)
data class Feat19StateBlock2(val state: Feat19UiModel, val checksum: Int)
data class Feat19StateBlock3(val state: Feat19UiModel, val checksum: Int)
data class Feat19StateBlock4(val state: Feat19UiModel, val checksum: Int)
data class Feat19StateBlock5(val state: Feat19UiModel, val checksum: Int)
data class Feat19StateBlock6(val state: Feat19UiModel, val checksum: Int)
data class Feat19StateBlock7(val state: Feat19UiModel, val checksum: Int)
data class Feat19StateBlock8(val state: Feat19UiModel, val checksum: Int)
data class Feat19StateBlock9(val state: Feat19UiModel, val checksum: Int)
data class Feat19StateBlock10(val state: Feat19UiModel, val checksum: Int)

fun buildFeat19UserItem(user: CoreUser, index: Int): Feat19UserItem1 {
    return Feat19UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat19StateBlock(model: Feat19UiModel): Feat19StateBlock1 {
    return Feat19StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat19UserSummary> {
    val list = java.util.ArrayList<Feat19UserSummary>(users.size)
    for (user in users) {
        list += Feat19UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat19UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat19UiModel {
    val summaries = (0 until count).map {
        Feat19UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat19UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat19UiModel> {
    val models = java.util.ArrayList<Feat19UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat19AnalyticsEvent1(val name: String, val value: String)
data class Feat19AnalyticsEvent2(val name: String, val value: String)
data class Feat19AnalyticsEvent3(val name: String, val value: String)
data class Feat19AnalyticsEvent4(val name: String, val value: String)
data class Feat19AnalyticsEvent5(val name: String, val value: String)
data class Feat19AnalyticsEvent6(val name: String, val value: String)
data class Feat19AnalyticsEvent7(val name: String, val value: String)
data class Feat19AnalyticsEvent8(val name: String, val value: String)
data class Feat19AnalyticsEvent9(val name: String, val value: String)
data class Feat19AnalyticsEvent10(val name: String, val value: String)

fun logFeat19Event1(event: Feat19AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat19Event2(event: Feat19AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat19Event3(event: Feat19AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat19Event4(event: Feat19AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat19Event5(event: Feat19AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat19Event6(event: Feat19AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat19Event7(event: Feat19AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat19Event8(event: Feat19AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat19Event9(event: Feat19AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat19Event10(event: Feat19AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat19Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat19Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat19Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat19Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat19Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat19Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat19Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat19Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat19Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat19Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat19(u: CoreUser): Feat19Projection1 =
    Feat19Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat19Projection1> {
    val list = java.util.ArrayList<Feat19Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat19(u)
    }
    return list
}
