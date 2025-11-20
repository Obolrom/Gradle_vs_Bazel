package com.romix.feature.feat274

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat274Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat274UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat274FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat274UserSummary
)

data class Feat274UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat274NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat274Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat274Config = Feat274Config()
) {

    fun loadSnapshot(userId: Long): Feat274NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat274NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat274UserSummary {
        return Feat274UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat274FeedItem> {
        val result = java.util.ArrayList<Feat274FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat274FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat274UiMapper {

    fun mapToUi(model: List<Feat274FeedItem>): Feat274UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat274UiModel(
            header = UiText("Feat274 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat274UiModel =
        Feat274UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat274UiModel =
        Feat274UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat274UiModel =
        Feat274UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat274Service(
    private val repository: Feat274Repository,
    private val uiMapper: Feat274UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat274UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat274UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat274UserItem1(val user: CoreUser, val label: String)
data class Feat274UserItem2(val user: CoreUser, val label: String)
data class Feat274UserItem3(val user: CoreUser, val label: String)
data class Feat274UserItem4(val user: CoreUser, val label: String)
data class Feat274UserItem5(val user: CoreUser, val label: String)
data class Feat274UserItem6(val user: CoreUser, val label: String)
data class Feat274UserItem7(val user: CoreUser, val label: String)
data class Feat274UserItem8(val user: CoreUser, val label: String)
data class Feat274UserItem9(val user: CoreUser, val label: String)
data class Feat274UserItem10(val user: CoreUser, val label: String)

data class Feat274StateBlock1(val state: Feat274UiModel, val checksum: Int)
data class Feat274StateBlock2(val state: Feat274UiModel, val checksum: Int)
data class Feat274StateBlock3(val state: Feat274UiModel, val checksum: Int)
data class Feat274StateBlock4(val state: Feat274UiModel, val checksum: Int)
data class Feat274StateBlock5(val state: Feat274UiModel, val checksum: Int)
data class Feat274StateBlock6(val state: Feat274UiModel, val checksum: Int)
data class Feat274StateBlock7(val state: Feat274UiModel, val checksum: Int)
data class Feat274StateBlock8(val state: Feat274UiModel, val checksum: Int)
data class Feat274StateBlock9(val state: Feat274UiModel, val checksum: Int)
data class Feat274StateBlock10(val state: Feat274UiModel, val checksum: Int)

fun buildFeat274UserItem(user: CoreUser, index: Int): Feat274UserItem1 {
    return Feat274UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat274StateBlock(model: Feat274UiModel): Feat274StateBlock1 {
    return Feat274StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat274UserSummary> {
    val list = java.util.ArrayList<Feat274UserSummary>(users.size)
    for (user in users) {
        list += Feat274UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat274UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat274UiModel {
    val summaries = (0 until count).map {
        Feat274UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat274UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat274UiModel> {
    val models = java.util.ArrayList<Feat274UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat274AnalyticsEvent1(val name: String, val value: String)
data class Feat274AnalyticsEvent2(val name: String, val value: String)
data class Feat274AnalyticsEvent3(val name: String, val value: String)
data class Feat274AnalyticsEvent4(val name: String, val value: String)
data class Feat274AnalyticsEvent5(val name: String, val value: String)
data class Feat274AnalyticsEvent6(val name: String, val value: String)
data class Feat274AnalyticsEvent7(val name: String, val value: String)
data class Feat274AnalyticsEvent8(val name: String, val value: String)
data class Feat274AnalyticsEvent9(val name: String, val value: String)
data class Feat274AnalyticsEvent10(val name: String, val value: String)

fun logFeat274Event1(event: Feat274AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat274Event2(event: Feat274AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat274Event3(event: Feat274AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat274Event4(event: Feat274AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat274Event5(event: Feat274AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat274Event6(event: Feat274AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat274Event7(event: Feat274AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat274Event8(event: Feat274AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat274Event9(event: Feat274AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat274Event10(event: Feat274AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat274Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat274Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat274Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat274Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat274Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat274Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat274Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat274Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat274Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat274Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat274(u: CoreUser): Feat274Projection1 =
    Feat274Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat274Projection1> {
    val list = java.util.ArrayList<Feat274Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat274(u)
    }
    return list
}
