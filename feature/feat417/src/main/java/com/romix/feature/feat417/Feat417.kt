package com.romix.feature.feat417

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat417Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat417UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat417FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat417UserSummary
)

data class Feat417UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat417NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat417Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat417Config = Feat417Config()
) {

    fun loadSnapshot(userId: Long): Feat417NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat417NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat417UserSummary {
        return Feat417UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat417FeedItem> {
        val result = java.util.ArrayList<Feat417FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat417FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat417UiMapper {

    fun mapToUi(model: List<Feat417FeedItem>): Feat417UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat417UiModel(
            header = UiText("Feat417 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat417UiModel =
        Feat417UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat417UiModel =
        Feat417UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat417UiModel =
        Feat417UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat417Service(
    private val repository: Feat417Repository,
    private val uiMapper: Feat417UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat417UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat417UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat417UserItem1(val user: CoreUser, val label: String)
data class Feat417UserItem2(val user: CoreUser, val label: String)
data class Feat417UserItem3(val user: CoreUser, val label: String)
data class Feat417UserItem4(val user: CoreUser, val label: String)
data class Feat417UserItem5(val user: CoreUser, val label: String)
data class Feat417UserItem6(val user: CoreUser, val label: String)
data class Feat417UserItem7(val user: CoreUser, val label: String)
data class Feat417UserItem8(val user: CoreUser, val label: String)
data class Feat417UserItem9(val user: CoreUser, val label: String)
data class Feat417UserItem10(val user: CoreUser, val label: String)

data class Feat417StateBlock1(val state: Feat417UiModel, val checksum: Int)
data class Feat417StateBlock2(val state: Feat417UiModel, val checksum: Int)
data class Feat417StateBlock3(val state: Feat417UiModel, val checksum: Int)
data class Feat417StateBlock4(val state: Feat417UiModel, val checksum: Int)
data class Feat417StateBlock5(val state: Feat417UiModel, val checksum: Int)
data class Feat417StateBlock6(val state: Feat417UiModel, val checksum: Int)
data class Feat417StateBlock7(val state: Feat417UiModel, val checksum: Int)
data class Feat417StateBlock8(val state: Feat417UiModel, val checksum: Int)
data class Feat417StateBlock9(val state: Feat417UiModel, val checksum: Int)
data class Feat417StateBlock10(val state: Feat417UiModel, val checksum: Int)

fun buildFeat417UserItem(user: CoreUser, index: Int): Feat417UserItem1 {
    return Feat417UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat417StateBlock(model: Feat417UiModel): Feat417StateBlock1 {
    return Feat417StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat417UserSummary> {
    val list = java.util.ArrayList<Feat417UserSummary>(users.size)
    for (user in users) {
        list += Feat417UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat417UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat417UiModel {
    val summaries = (0 until count).map {
        Feat417UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat417UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat417UiModel> {
    val models = java.util.ArrayList<Feat417UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat417AnalyticsEvent1(val name: String, val value: String)
data class Feat417AnalyticsEvent2(val name: String, val value: String)
data class Feat417AnalyticsEvent3(val name: String, val value: String)
data class Feat417AnalyticsEvent4(val name: String, val value: String)
data class Feat417AnalyticsEvent5(val name: String, val value: String)
data class Feat417AnalyticsEvent6(val name: String, val value: String)
data class Feat417AnalyticsEvent7(val name: String, val value: String)
data class Feat417AnalyticsEvent8(val name: String, val value: String)
data class Feat417AnalyticsEvent9(val name: String, val value: String)
data class Feat417AnalyticsEvent10(val name: String, val value: String)

fun logFeat417Event1(event: Feat417AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat417Event2(event: Feat417AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat417Event3(event: Feat417AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat417Event4(event: Feat417AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat417Event5(event: Feat417AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat417Event6(event: Feat417AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat417Event7(event: Feat417AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat417Event8(event: Feat417AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat417Event9(event: Feat417AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat417Event10(event: Feat417AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat417Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat417Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat417Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat417Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat417Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat417Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat417Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat417Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat417Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat417Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat417(u: CoreUser): Feat417Projection1 =
    Feat417Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat417Projection1> {
    val list = java.util.ArrayList<Feat417Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat417(u)
    }
    return list
}
