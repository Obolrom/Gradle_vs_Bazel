package com.romix.feature.feat264

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat264Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat264UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat264FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat264UserSummary
)

data class Feat264UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat264NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat264Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat264Config = Feat264Config()
) {

    fun loadSnapshot(userId: Long): Feat264NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat264NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat264UserSummary {
        return Feat264UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat264FeedItem> {
        val result = java.util.ArrayList<Feat264FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat264FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat264UiMapper {

    fun mapToUi(model: List<Feat264FeedItem>): Feat264UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat264UiModel(
            header = UiText("Feat264 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat264UiModel =
        Feat264UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat264UiModel =
        Feat264UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat264UiModel =
        Feat264UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat264Service(
    private val repository: Feat264Repository,
    private val uiMapper: Feat264UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat264UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat264UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat264UserItem1(val user: CoreUser, val label: String)
data class Feat264UserItem2(val user: CoreUser, val label: String)
data class Feat264UserItem3(val user: CoreUser, val label: String)
data class Feat264UserItem4(val user: CoreUser, val label: String)
data class Feat264UserItem5(val user: CoreUser, val label: String)
data class Feat264UserItem6(val user: CoreUser, val label: String)
data class Feat264UserItem7(val user: CoreUser, val label: String)
data class Feat264UserItem8(val user: CoreUser, val label: String)
data class Feat264UserItem9(val user: CoreUser, val label: String)
data class Feat264UserItem10(val user: CoreUser, val label: String)

data class Feat264StateBlock1(val state: Feat264UiModel, val checksum: Int)
data class Feat264StateBlock2(val state: Feat264UiModel, val checksum: Int)
data class Feat264StateBlock3(val state: Feat264UiModel, val checksum: Int)
data class Feat264StateBlock4(val state: Feat264UiModel, val checksum: Int)
data class Feat264StateBlock5(val state: Feat264UiModel, val checksum: Int)
data class Feat264StateBlock6(val state: Feat264UiModel, val checksum: Int)
data class Feat264StateBlock7(val state: Feat264UiModel, val checksum: Int)
data class Feat264StateBlock8(val state: Feat264UiModel, val checksum: Int)
data class Feat264StateBlock9(val state: Feat264UiModel, val checksum: Int)
data class Feat264StateBlock10(val state: Feat264UiModel, val checksum: Int)

fun buildFeat264UserItem(user: CoreUser, index: Int): Feat264UserItem1 {
    return Feat264UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat264StateBlock(model: Feat264UiModel): Feat264StateBlock1 {
    return Feat264StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat264UserSummary> {
    val list = java.util.ArrayList<Feat264UserSummary>(users.size)
    for (user in users) {
        list += Feat264UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat264UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat264UiModel {
    val summaries = (0 until count).map {
        Feat264UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat264UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat264UiModel> {
    val models = java.util.ArrayList<Feat264UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat264AnalyticsEvent1(val name: String, val value: String)
data class Feat264AnalyticsEvent2(val name: String, val value: String)
data class Feat264AnalyticsEvent3(val name: String, val value: String)
data class Feat264AnalyticsEvent4(val name: String, val value: String)
data class Feat264AnalyticsEvent5(val name: String, val value: String)
data class Feat264AnalyticsEvent6(val name: String, val value: String)
data class Feat264AnalyticsEvent7(val name: String, val value: String)
data class Feat264AnalyticsEvent8(val name: String, val value: String)
data class Feat264AnalyticsEvent9(val name: String, val value: String)
data class Feat264AnalyticsEvent10(val name: String, val value: String)

fun logFeat264Event1(event: Feat264AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat264Event2(event: Feat264AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat264Event3(event: Feat264AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat264Event4(event: Feat264AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat264Event5(event: Feat264AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat264Event6(event: Feat264AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat264Event7(event: Feat264AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat264Event8(event: Feat264AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat264Event9(event: Feat264AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat264Event10(event: Feat264AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat264Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat264Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat264Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat264Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat264Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat264Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat264Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat264Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat264Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat264Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat264(u: CoreUser): Feat264Projection1 =
    Feat264Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat264Projection1> {
    val list = java.util.ArrayList<Feat264Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat264(u)
    }
    return list
}
